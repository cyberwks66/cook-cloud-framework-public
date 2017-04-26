package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.core.RouteVersionDetails;
import com.cooksys.cloud.router.core.TrafficRatioManager;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.util.ContextHelper;
import com.netflix.zuul.ZuulFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter that is responsible for weighted routing to specific versions.  Can handle canary and blue-green use cases.
 *
 * @author Tim Davidson
 */
public class TrafficRatioFilter extends ZuulFilter {
    private static final Logger logger = LoggerFactory.getLogger(TrafficRatioFilter.class);

    private final RequestContextFactory contextFactory;
    private final TrafficRatioManager trafficRatioManager;
    private final RouterProperties config;

    public TrafficRatioFilter(RequestContextFactory contextFactory, TrafficRatioManager trafficRatioManager, RouterProperties config) {
        this.contextFactory = contextFactory;
        this.trafficRatioManager = trafficRatioManager;
        this.config = config;
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.TRAFFIC_RATIO_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.TRAFFIC_RATIO_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        String serviceId = null;
        if (contextFactory.getZuulRequestContext().get(ContextConstants.ROUTE_SERVICE_ID) instanceof String) {
            serviceId = (String) contextFactory.getZuulRequestContext().get(ContextConstants.ROUTE_SERVICE_ID);
        }

        return config.isEdgeProxyMode() &&
                serviceId != null &&
                trafficRatioManager.configuredRatiosExist(serviceId);
    }

    @Override
    public Object run() {
        logger.info("executing TrafficRatioFilter");
        try {
            String serviceId = null;
            if (contextFactory.getZuulRequestContext().get(ContextConstants.ROUTE_SERVICE_ID) instanceof String) {
                serviceId = (String) contextFactory.getZuulRequestContext().get(ContextConstants.ROUTE_SERVICE_ID);
            }

            if (serviceId == null) {
                logger.warn("ServiceId was never set - aborting filter");
                return null;
            }

            final RouteVersionDetails nextRoute = trafficRatioManager.getNextRouteVersionDetails(serviceId);

            if (nextRoute == null) {
                logger.warn("TrafficRatioManager.getNextRouteVersionDetails() returned null - aborting filter");
                return null;
            }

            contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_VERSION, nextRoute.getVersion());
            contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_VERSION_ACCURACY, nextRoute.getSemanticAccuracy());

            if (nextRoute.getExcludedVersions() != null && !nextRoute.getExcludedVersions().isEmpty()) {
                contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_EXCLUDED_VERSIONS, nextRoute.getExcludedVersions());
            }

        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(),500,e);
        }
        return null;
    }
}