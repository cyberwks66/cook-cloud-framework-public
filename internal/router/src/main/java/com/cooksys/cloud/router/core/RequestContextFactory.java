package com.cooksys.cloud.router.core;

import com.netflix.zuul.context.RequestContext;

/**
 * Wraps zuul's static RequestContext into a managed component to ease unit testing
 *
 * @author Tim Davidson
 */
public class RequestContextFactory {
    public RequestContext getZuulRequestContext() {
        return RequestContext.getCurrentContext();
    }
}
