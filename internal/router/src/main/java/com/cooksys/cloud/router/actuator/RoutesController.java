package com.cooksys.cloud.router.actuator;

import com.cooksys.cloud.commons.event.router.ConfigureRouteBusEvent;
import com.cooksys.cloud.commons.event.router.ConfigureTrafficRatioBusEvent;
import com.cooksys.cloud.router.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Actuator REST API for dynamically configuring routes
 *
 * @author Tim Davidson
 */
public class RoutesController extends EndpointMvcAdapter {
    public static final String RESPONSE_CODE = "responseCode";
    public static final String OK = "200";
    public static final String NOT_FOUND = "404";
    public static final String RESPONSE_MSG = "responseMsg";
    public static final String SUCCESS = "Success";
    public static final String ROUTES = "routes";
    public static final String ROUTE = "route";

    private final RouteManager routeManager;
    private final TrafficRatioManager ratioManager;
    private final ApplicationContext context;

    @Autowired
    public RoutesController(Endpoint<?> delegate, RouteManager routeManager, TrafficRatioManager ratioManager, ApplicationContext context) {
        super(delegate);
        this.routeManager = routeManager;
        this.ratioManager = ratioManager;
        this.context = context;
    }

    /**
     * GET /routes endpoint - returns a list of all configured routes
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoutes(HttpServletRequest request) {
        final Map<String, Object> response = new LinkedHashMap<>();
        final List<ApiRoute> apiRoutes = new ArrayList<>();

        routeManager.getRoutes().forEach(dynamicRoute -> {
            apiRoutes.add(buildApiRoute(dynamicRoute, request));
        });

        response.put(RESPONSE_CODE, OK);
        response.put(RESPONSE_MSG, SUCCESS);
        response.put(ROUTES, apiRoutes);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * POST /routes endpoint
     *
     * @param route
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRoute(@RequestBody @Valid ApiRoute route, HttpServletRequest request) {
        final Map<String, Object> response = new LinkedHashMap<>();

        // Push this route to the event bus
        ConfigureRouteBusEvent event = new ConfigureRouteBusEvent(this, route.getServiceId(),
                route.getvHost(), route.getDefaultVersion(), route.getProxyRoutes());
        context.publishEvent(event);

        route.setLink(request.getRequestURL().toString() + "/" + route.getServiceId());
        response.put(RESPONSE_CODE, OK);
        response.put(RESPONSE_MSG, SUCCESS);
        response.put(ROUTE, route);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * PUT /routes/{serviceId} endpoint
     *
     * @param serviceId
     * @param apiRoute
     * @param request
     * @return
     */
    @RequestMapping(value = "{serviceId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> modifyRoute(@PathVariable String serviceId, @RequestBody ApiRoute apiRoute, HttpServletRequest request) {
        final Map<String, Object> response = new LinkedHashMap<>();
        Optional<DynamicRoute> matchedRoute = routeManager.getRoutes().stream().filter(x -> x.getServiceId().equals(serviceId)).findFirst();

        if (!matchedRoute.isPresent()) {
            response.put(RESPONSE_CODE, NOT_FOUND);
            response.put(RESPONSE_MSG, "Route for serviceId: " + serviceId + " not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        final String defaultVersion = hasValue(apiRoute.getDefaultVersion()) ?
                apiRoute.getDefaultVersion() : matchedRoute.get().getDefaultVersion().toString();

        final String vHost = hasValue(apiRoute.getvHost()) ?
                apiRoute.getvHost() : matchedRoute.get().getDefaultVersion().toString();

        final Map<String, String> proxyRoutes = hasValue(apiRoute.getProxyRoutes()) ?
                apiRoute.getProxyRoutes() : matchedRoute.get().getProxyRoutes().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toString()));

        ConfigureRouteBusEvent event = new ConfigureRouteBusEvent(this, serviceId, vHost, defaultVersion, proxyRoutes);

        context.publishEvent(event);
        response.put(RESPONSE_CODE, OK);
        response.put(RESPONSE_MSG, SUCCESS);

        final List<ApiRoute> apiRoutes = new ArrayList<>();

        routeManager.getRoutes().forEach(dynamicRoute -> {
            apiRoutes.add(buildApiRoute(dynamicRoute, request));
        });

        response.put(ROUTE, apiRoutes.stream().filter(e -> e.getServiceId().equals(serviceId)).findFirst().get());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /routes/{serviceId} endpoint
     *
     * @param serviceId
     * @param request
     * @return
     */
    @RequestMapping(value = "{serviceId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoute(@PathVariable String serviceId, HttpServletRequest request) {
        final Map<String, Object> response = new LinkedHashMap<>();
        final List<ApiRoute> apiRoutes = new ArrayList<>();

        routeManager.getRoutes().forEach(dynamicRoute -> {
            apiRoutes.add(buildApiRoute(dynamicRoute, request));
        });

        Optional<ApiRoute> matchedRoute = apiRoutes.stream().filter(e -> e.getServiceId().equals(serviceId)).findFirst();

        if (!matchedRoute.isPresent()) {
            response.put(RESPONSE_CODE, NOT_FOUND);
            response.put(RESPONSE_MSG, "Route for serviceId: " + serviceId + " not found");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        response.put(RESPONSE_CODE, OK);
        response.put(RESPONSE_MSG, SUCCESS);
        response.put(ROUTE, matchedRoute.get());
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "{serviceId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteRoute(@PathVariable String serviceId) {
        return null;
    }


    /**
     * GET /routes/{serviceId}/ratios endpoint
     *
     * @param serviceId
     * @return
     */
    @RequestMapping(value = "{serviceId}/ratios", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getServiceRatios(@PathVariable String serviceId) {
        final Map<String, Object> response = new LinkedHashMap<>();

        Map<RouteVersionDetails, Integer> ratioDetails = ratioManager.getRatioMap(serviceId);

        if (ratioDetails == null || ratioDetails.isEmpty()) {
            response.put(RESPONSE_CODE, NOT_FOUND);
            response.put(RESPONSE_MSG, "No ratios found for serviceId: " + serviceId);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        List<ConfigureTrafficRatioBusEvent.Ratio> ratios = ratioDetails.entrySet().stream().map(es -> {
            List<String> excludedVersions = null;

            if (es.getKey().getExcludedVersions() != null) {
                excludedVersions = es.getKey().getExcludedVersions()
                        .stream()
                        .map(v -> v.toString())
                        .collect(Collectors.toList());
            }

            String accuracy = accuracyToString(es.getKey().getSemanticAccuracy());

            return new ConfigureTrafficRatioBusEvent.Ratio(es.getKey().getVersion().toString(), es.getValue(), accuracy, excludedVersions);
        }).collect(Collectors.toList());

        response.put(RESPONSE_CODE, OK);
        response.put(RESPONSE_MSG, SUCCESS);
        response.put("ratios", ratios);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    /**
     * PUST /routes/{serviceId}/ratios endpoint
     *
     * @param serviceId
     * @param ratios
     * @return
     */
    @RequestMapping(value = "{serviceId}/ratios", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createNewRatio(@PathVariable String serviceId,
                                                               @RequestBody @Valid List<ConfigureTrafficRatioBusEvent.Ratio> ratios) {
        final Map<String, Object> response = new LinkedHashMap<>();

        // Make sure there is a Route entry for this serviceId
        if (routeManager.getRoutes().stream().filter(r -> r.getServiceId().equals(serviceId)).count() == 0L) {
            response.put(RESPONSE_CODE, NOT_FOUND);
            response.put(RESPONSE_MSG, "Route not configured for serviceId: " + serviceId);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        final ConfigureTrafficRatioBusEvent event = new ConfigureTrafficRatioBusEvent(this,serviceId,ratios);
        context.publishEvent(event);

        return getServiceRatios(serviceId);
    }

    /**
     * DELETE /routes/{serviceId}/ratios endpoint
     *
     * @param serviceId
     * @return
     */
    @RequestMapping(value = "{serviceId}/ratios", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRatio(@PathVariable String serviceId) {
        final Map<String, Object> response = new LinkedHashMap<>();

        ratioManager.deleteRatio(serviceId);

        response.put(RESPONSE_CODE, OK);
        response.put(RESPONSE_MSG, SUCCESS);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    private ApiRoute buildApiRoute(DynamicRoute dynamicRoute, HttpServletRequest request) {
        final ApiRoute apiRoute = new ApiRoute();
        apiRoute.setServiceId(dynamicRoute.getServiceId());

        apiRoute.setLink(request.getRequestURL() + "/" + dynamicRoute.getServiceId());
        apiRoute.setvHost(dynamicRoute.getvHost());
        apiRoute.setDefaultVersion(dynamicRoute.getDefaultVersion().toString());

        Map<String, String> proxyRoutes =
                dynamicRoute.getProxyRoutes().entrySet().stream().collect(
                        Collectors.toMap(e -> e.getKey(), e -> e.getValue().toString()));

        apiRoute.setProxyRoutes(proxyRoutes);
        return apiRoute;
    }

    private static boolean hasValue(Object parameter) {
        if (parameter instanceof String) {
            return parameter != null && !((String) parameter).isEmpty();
        } else if (parameter instanceof Map) {
            return parameter != null && !((Map) parameter).isEmpty();
        }
        return false;
    }

    public static String accuracyToString(SemanticAccuracy accuracy) {
        switch (accuracy) {
            case MAJOR:
                return "MAJOR";
            case MINOR:
                return "MINOR";
            default:
                return "PATCH";
        }
    }

    public static SemanticAccuracy stringToAccuracy(String accuracy) {
        switch (accuracy) {
            case "MAJOR":
                return SemanticAccuracy.MAJOR;
            case "MINOR":
                return SemanticAccuracy.MINOR;
            default:
                return SemanticAccuracy.PATCH;
        }
    }

}