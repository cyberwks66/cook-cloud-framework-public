package com.cooksys.cloud.sdk;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Request-scoped bean that provides an easy way to store key value pairs that
 * can be shared by classes inside a request context as well as filters. It also
 * generates a unique contextId that can be used for logging and debugging. To
 * use this bean, simply autowire it into your request-scoped beans and filters.
 * <p>
 * @author Tim Davidson
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CloudRequestContext extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    private String contextId;

    /**
     * Returns a unique contextId that is attached to the current request
     * context
     *
     * @return
     */
    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

}
