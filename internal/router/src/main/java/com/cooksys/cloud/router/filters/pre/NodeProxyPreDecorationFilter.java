package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.*;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.util.ContextHelper;
import com.github.zafarkhaja.semver.Version;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * This filter is only active in Node Proxy mode and is the first filter to execute in the filter chain.  When the
 * request comes in, it will find a matching route based on the url path and places the route details in the request
 * context, and strips off the prefix from the forward path.
 *
 * @author Tim Davidson
 */
public class NodeProxyPreDecorationFilter extends PreDecorationFilter {
    private static final Logger logger = LoggerFactory.getLogger(NodeProxyPreDecorationFilter.class);

    public NodeProxyPreDecorationFilter(RouteManager routeManager, RequestContextFactory contextFactory, RouterProperties config) {
        super(routeManager, contextFactory, config);
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
        return !config.isEdgeProxyMode();
    }

    @Override
    public Object run() {
        logger.info("executing NodeProxyPreDecorationFilter");
        try {
            final RequestContext context = contextFactory.getZuulRequestContext();
            final String servletPath = context.getRequest().getServletPath();

            final Optional<DynamicRoute> route = routeManager.getMatchingRoute(servletPath);

            if (!route.isPresent()) {
                ContextHelper.errorResponse(context, 500, new RuntimeException("Could not find a matching route - servletPath: " + servletPath));
                return null;
            }

            Version routeVersion = null;
            String stripPrefix = null;

            for (Map.Entry<String, Version> proxyRouteEntry : route.get().getProxyRoutes().entrySet()) {
                if (servletPath.startsWith(proxyRouteEntry.getKey())) {
                    routeVersion = proxyRouteEntry.getValue();
                    stripPrefix = proxyRouteEntry.getKey();
                }
            }

            if (routeVersion == null || stripPrefix == null) {
                ContextHelper.errorResponse(context, 500, new RuntimeException("Configured route is missing information"));
                return null;
            }

            context.put(ContextConstants.ROUTE_SERVICE_ID, route.get().getServiceId());
            context.put(ContextConstants.ROUTE_VERSION, routeVersion);

            // exact version has already been determined by the Edge proxy load balancer, so set accuracy to PATCH
            context.put(ContextConstants.ROUTE_VERSION_ACCURACY, SemanticAccuracy.PATCH);

            context.put(ContextConstants.ROUTE_PATH, servletPath.replace(stripPrefix, ""));

        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(), 500, e);
        }
        return null;
    }
}
