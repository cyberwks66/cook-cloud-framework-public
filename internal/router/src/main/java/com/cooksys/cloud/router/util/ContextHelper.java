package com.cooksys.cloud.router.util;

import com.cooksys.cloud.router.core.ContextConstants;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * Utility class for dealing with zuul's request context
 *
 * @author Tim Davidson
 */
public class ContextHelper {

    /**
     * Helper method that places an exception in the context so that Spring Cloud's SendErrorFilter
     * will send the appropriate error response.
     *
     * - note that in spring-cloud-core version 1.2.6 uses these context parameters, but in future releases (1.3.x), the
     criteria for shouldFilter() checks for the exception using context.getThrowable() and expects ZuulException,
     which contains the status code.  In order to support both versions, when an exception is caught in a filter,
     we need to put the exception in the context in 2 places: 1) wrap the throwable in a ZuulException and put it in
     the context.setThrowable(), as well as placing the throwable in error.exception, and setting the error.status_code
     property in the context.

     * @param context Zuul request context
     * @param statusCode - http status code
     * @param throwable - exception to be added to the context
     */
    public static void errorResponse(RequestContext context, int statusCode, Throwable throwable) {
        context.set(ContextConstants.ERROR_STATUS_CODE,statusCode);
        context.set(ContextConstants.ERROR_EXCEPTION,throwable);

        context.setThrowable(new ZuulException(throwable,throwable.getMessage(),500,throwable.getMessage()));
    }
}
