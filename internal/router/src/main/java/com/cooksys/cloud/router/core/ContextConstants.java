package com.cooksys.cloud.router.core;

/**
 * RequestContext parameters - these are the keys used when setting context parameters.  Zuul's RequestContext
 * extends ConcurrentHashMap<String, Object>, which allows us to set K/V pairs in the context for passing values to
 * subsequent filters in the filter chain.
 *
 * @author Tim Davidson
 */
public class ContextConstants {

    /*
        Route constants
     */
    public static final String ROUTE_SERVICE_ID = "route.serviceId"; // String
    public static final String ROUTE_VERSION = "route.version"; // SemanticVersion object
    public static final String ROUTE_VERSION_ACCURACY="route.versionAccuracy"; // SemanticAccuracy enum
    public static final String ROUTE_PATH = "route.path"; // String
    public static final String ROUTE_PROXY_ROUTES = "route.proxyRoutes";
    public static final String ROUTE_EXCLUDED_VERSIONS = "route.exludedVersions";

    /*
        SimpleRoute constants
     */
    public static final String SIMPLE_ROUTE_HOST = "simpleRoute.host"; // String
    public static final String SIMPLE_ROUTE_PORT = "simpleRoute.port"; // String
    public static final String SIMPLE_ROUTE_PATH = "simpleRoute.path"; // Integer

    /*
        Request constants
     */
    public static final String REQUEST_VERSION = "request.version"; // SemanticVersion object

    /*
        Discovery metadata constants
     */
    public static final String METADATA_VERSION_MAJOR = "version.major";
    public static final String METADATA_VERSION_MINOR = "version.minor";
    public static final String METADATA_VERSION_PATCH = "version.patch";

    /*
        Used by spring cloud's SendErrorFilter
     */

    public static final String ERROR_STATUS_CODE="error.status_code";
    public static final String ERROR_EXCEPTION="error.exception";
    public static final String STOPWATCH = "stopwatch";

    private ContextConstants() {}
}