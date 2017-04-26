package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.*;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.util.ContextHelper;
import com.cooksys.cloud.router.util.VersionedUrlPath;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * This filter is only active in Edge Proxy mode and is the first filter to execute in the filter chain.  When the
 * request comes in, it will find a matching route based on the host header and places the route details in the request
 * context.  In addition, if the version parameter is found in the query string, it will place route version in the
 * request context.
 *
 */
public class EdgeProxyPreDecorationFilter extends PreDecorationFilter {
    private static final Logger logger = LoggerFactory.getLogger(EdgeProxyPreDecorationFilter.class);

    public EdgeProxyPreDecorationFilter(RouteManager routeManager, RequestContextFactory contextFactory, RouterProperties config) {
        super(routeManager,contextFactory,config);
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.PRE_DECORATION_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.PRE_DECORATION_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return config.isEdgeProxyMode();
    }

    @Override
    public Object run() {
        logger.info("executing EdgeProxyPreDecorationFilter");

        try {
            final RequestContext context = contextFactory.getZuulRequestContext();

            final String hostHeader = context.getRequest().getHeader("host");

            final Optional<DynamicRoute> route = routeManager.getMatchingRoute(hostHeader);

            if (!route.isPresent()) {
                ContextHelper.errorResponse(context,500,new RuntimeException("Could not find a matching route - host header: " + hostHeader));
                return null;
            }

            VersionedUrlPath versionedUrlPath = null;
            final String path = context.getRequest().getServletPath();
            try {
                // check for version prefix in the url path - /_/{major}/{minor}/{patch}/_/
                versionedUrlPath = VersionedUrlPath.valueOf(context.getRequest().getServletPath());
                context.put(ContextConstants.REQUEST_VERSION, versionedUrlPath.getVersion());
                context.put(ContextConstants.ROUTE_VERSION,versionedUrlPath.getVersion());
                context.put(ContextConstants.ROUTE_PATH,versionedUrlPath.getPathRemainder());
            } catch (IllegalArgumentException e) {
                logger.debug("Non-versioned path detected - using route default version: " + route.get().getDefaultVersion());
                //use default version configured in the route
                if(route.get().getDefaultVersion() != null) {
                    context.put(ContextConstants.ROUTE_VERSION,route.get().getDefaultVersion());
                }
                context.put(ContextConstants.ROUTE_PATH,path);
            }

            context.put(ContextConstants.ROUTE_VERSION_ACCURACY, SemanticAccuracy.MAJOR);
            context.put(ContextConstants.ROUTE_PROXY_ROUTES,route.get().getProxyRoutes());
            context.put(ContextConstants.ROUTE_SERVICE_ID, route.get().getServiceId());

        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(),500,e);
        }
        return null;
    }
}



















