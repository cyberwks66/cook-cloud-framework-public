package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.util.ContextHelper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple route locator filter used in reverse proxy of http requests
 *
 * @author Tim Davidson
 */
public class SimpleRouteLocatorFilter extends ZuulFilter {
    public static final Logger logger = LoggerFactory.getLogger(SimpleRouteLocatorFilter.class);

    private final RequestContextFactory contextFactory;
    private final RouterProperties config;

    public SimpleRouteLocatorFilter(RequestContextFactory contextFactory, RouterProperties config) {
        this.contextFactory = contextFactory;
        this.config = config;
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.SIMPLE_ROUTE_LOCATOR_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.SIMPLE_ROUTE_LOCATOR_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() {
        logger.info("executing SimpleRouteLocatorFilter");

        try {
            final RequestContext context = contextFactory.getZuulRequestContext();

            final String servletPath = context.getRequest().getServletPath();

            RouterProperties.SimpleProxy.SimpleRoute route = null;

            for (RouterProperties.SimpleProxy.SimpleRoute routeEntry : config.getSimpleProxy().getRoutes().values()) {
                if (servletPath.startsWith(routeEntry.getContextPath())) {
                    route = routeEntry;
                }
            }

            if (route != null) {
                context.put(ContextConstants.SIMPLE_ROUTE_PATH, stripContextPrefix(servletPath, route.getContextPath()));
                context.put(ContextConstants.SIMPLE_ROUTE_HOST, route.getProxyHost());
                context.put(ContextConstants.SIMPLE_ROUTE_PORT, route.getProxyPort());
            }

        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(),500,e);
        }
        return null;

    }

    private static String stripContextPrefix(String servletPath, String context) {
        if (context.endsWith("/")) {
            context = context.substring(0, context.length() - 1);
        }
        return servletPath.substring(context.length());
    }

}
