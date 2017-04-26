package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.netflix.zuul.ZuulFilter;

/**
 * Abstract class to define Pre-decoration filters
 *
 * @author Tim Davidson
 */
public abstract class PreDecorationFilter extends ZuulFilter {
    protected final RouteManager routeManager;
    protected final RequestContextFactory contextFactory;
    protected final RouterProperties config;

    public PreDecorationFilter(RouteManager routeManager, RequestContextFactory contextFactory, RouterProperties config) {
        this.routeManager = routeManager;
        this.contextFactory=contextFactory;
        this.config=config;
    }
}
