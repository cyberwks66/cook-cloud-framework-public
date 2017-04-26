package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.filters.route.hystrix.HystrixMetadata;
import com.cooksys.cloud.router.filters.route.hystrix.RestTemplateHystrixCommand;
import com.cooksys.cloud.router.filters.route.ribbon.VersionAwareRibbonClient;
import com.cooksys.cloud.router.util.ContextHelper;
import com.github.zafarkhaja.semver.ParseException;
import com.github.zafarkhaja.semver.Version;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Routing filter that uses {@link org.springframework.web.client.RestTemplate} to proxy requests
 *
 * @author Tim Davidson
 */
public class LoadBalancedRoutingFilter extends AbstractRoutingFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancedRoutingFilter.class);

    private static final String HTTP = "http";
    private static final String PROXY_HOST = "proxyHost";
    private static final String HTTPS = "https";

    private final VersionAwareRibbonClient ribbonClient;
    private final RouterProperties config;
    private final RestOperations restTemplate;

    private final String hostIp;

    @Value("${eureka.instance.hostName}")
    private String proxyHost;

    public LoadBalancedRoutingFilter(RequestContextFactory contextFactory, VersionAwareRibbonClient ribbonClient, RouterProperties config, RestOperations restTemplate, String hostIp) {
        super(contextFactory);
        this.ribbonClient = ribbonClient;
        this.config = config;
        this.restTemplate = restTemplate;
        this.hostIp = hostIp;
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.LOAD_BALANCED_ROUTING_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.LOAD_BALANCED_ROUTING_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        final RequestContext context = contextFactory.getZuulRequestContext();
        return context.get(ContextConstants.ROUTE_SERVICE_ID) != null;
    }

    @Override
    public Object run() {
        try {
            final RequestContext context = contextFactory.getZuulRequestContext();
            final String serviceId = (String) context.get(ContextConstants.ROUTE_SERVICE_ID);
            final Version semanticVersion = (Version) context.get(ContextConstants.ROUTE_VERSION);
            SemanticAccuracy accuracy = (SemanticAccuracy) context.get(ContextConstants.ROUTE_VERSION_ACCURACY);

            if (accuracy == null) {
                accuracy = SemanticAccuracy.MAJOR;
            }

            // Get the next service instance from the loadbalancer
            final ServiceInstance serviceInstance;

            if (context.get(ContextConstants.ROUTE_EXCLUDED_VERSIONS) != null) {
                final List<Version> excludedVersions = (List<Version>) context.get(ContextConstants.ROUTE_EXCLUDED_VERSIONS);
                serviceInstance = ribbonClient.getNextServerFromLoadBalancer(serviceId, semanticVersion, accuracy, excludedVersions);
            } else {
                if (config.isEdgeProxyMode()) {
                    serviceInstance = ribbonClient.getNextServerFromLoadBalancer(serviceId, semanticVersion, accuracy);
                } else {
                    serviceInstance = ribbonClient.getNextServerFromLoadBalancer(serviceId, semanticVersion, accuracy, proxyHost);
                }
            }

            if (serviceInstance == null) {
                ContextHelper.errorResponse(context, 500, new RuntimeException("No route available for serviceId: "
                        + serviceId + " version: " + (semanticVersion != null ? semanticVersion.toString() : "not specified")));
                return null;
            }

            if (config.isEdgeProxyMode()) {
                if (serviceInstance.getMetadata() == null || serviceInstance.getMetadata().get(PROXY_HOST) == null) {
                    ContextHelper.errorResponse(context, 500, new RuntimeException("Unable to route to service: "
                            + serviceId
                            + " - Router is configured for Edge node, but service instance metadata does not contain key: 'proxyHost'"));
                    return null;
                }
            }

            final HttpServletRequest request = context.getRequest();
            final HttpHeaders headers = buildZuulRequestHeaders(request);
            final HttpMethod verb = getVerb(request);

            InputStream requestEntity;
            try {
                requestEntity = request.getInputStream();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            // if routeUsingProxyHost is true, this means I am configured in edge mode
            // Route to the proxy instead of the container directly
            final String routeHost = config.isEdgeProxyMode() ?
                    serviceInstance.getMetadata().get(PROXY_HOST) : serviceInstance.getHost();
            String routePath = (String) context.get(ContextConstants.ROUTE_PATH);

            if (serviceInstance.getMetadata() == null || !serviceInstance.getMetadata().containsKey("version")) {
                ContextHelper.errorResponse(context, 500, new RuntimeException("Version metadata is missing from service entry"));
                return null;
            }

            Version serviceInstanceVersion;
            try {
                serviceInstanceVersion = Version.valueOf(serviceInstance.getMetadata().get("version"));
            } catch (IllegalArgumentException | ParseException e) {
                ContextHelper.errorResponse(context, 500, e);
                return null;
            }

            // In Edge proxy mode, prepend the proxy route path to the request path
            if (config.isEdgeProxyMode()) {
                @SuppressWarnings("unchecked") Map<String, Version> proxyRoutes = (Map<String, Version>) context.get(ContextConstants.ROUTE_PROXY_ROUTES);
                if (proxyRoutes == null || proxyRoutes.isEmpty()) {
                    ContextHelper.errorResponse(context, 500, new RuntimeException("Edge mode is active, but route is missing from the context"));
                    return null;
                }

                for (Map.Entry<String, Version> routeEntry : proxyRoutes.entrySet()) {
                    if (serviceInstanceVersion.equals(routeEntry.getValue())) {
                        routePath = routeEntry.getKey() + routePath;
                    }
                }
            }

            final URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(serviceInstance.isSecure() ? HTTPS : HTTP)
                    .setHost(routeHost)
                    .setPort(serviceInstance.getPort())
                    .setPath(routePath)
                    .setCustomQuery(request.getQueryString());

            final URI uri = uriBuilder.build();

            RequestEntity<Resource> httpEntity = null;
            // only set the requestEntity if the request is POST or PUT
            if (verb.equals(HttpMethod.POST) || verb.equals(HttpMethod.PUT)) {
                final Resource requestBody = new InputStreamResource(context.getRequest().getInputStream());
                httpEntity = new RequestEntity<Resource>(requestBody, headers, verb, uri);
            } else {
                httpEntity = new RequestEntity<Resource>(headers, verb, uri);
            }

            // forward the request wrapped in a hystrix circuit breaker if I am the edge host, otherwise, just forward the request
            logger.info("forwarding request to " + uri.toString());
            ResponseEntity<Resource> response;
            if (config.isEdgeProxyMode()) {
                final HystrixMetadata metadata = new HystrixMetadata(config.isEdgeProxyMode() ? serviceId : hostIp,
                        config.isEdgeProxyMode() ? serviceInstanceVersion.toString() : "", serviceId);


                final RestTemplateHystrixCommand hystrixCommand = new RestTemplateHystrixCommand(metadata, restTemplate, httpEntity, context);

                if (hystrixCommand.getRestTemplateException() != null) {
                    ContextHelper.errorResponse(contextFactory.getZuulRequestContext(), 500, hystrixCommand.getRestTemplateException());
                    return null;
                }
                response = hystrixCommand.execute();
                if (response == null) {
                    throw new RuntimeException("No response from backend proxy");
                }
            } else { // node-proxy
                response = restTemplate.exchange(httpEntity, Resource.class);

                if (response==null) {
                    throw new RuntimeException("No response from service");
                }
            }

            logger.info("setting response - status: " + response.getStatusCode().toString());
            setResponse(response);

        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(), 500, e);
        }
        return null;
    }
}