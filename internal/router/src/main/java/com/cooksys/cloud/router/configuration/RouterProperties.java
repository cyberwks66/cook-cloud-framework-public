package com.cooksys.cloud.router.configuration;

import com.cooksys.cloud.commons.event.router.ConfigureTrafficRatioBusEvent;
import com.google.common.base.MoreObjects;

import java.util.Map;

/**
 * Configuration properties for the router service
 *
 * @author Tim Davidson
 */
public class RouterProperties {

    /**
     * Router can be run in 2 modes, edge proxy or reverse proxy on a node - set this to true if using edge proxy mode.
     * When set to true, the routing filter will route using the Eureka metadata value for "proxyHost", rather than "host"
     */
    private boolean edgeProxyMode = false;

    /**
     *  Map of routes (key is serviceId)
     */
    private Map<String,Route> routes;

    /**
     * Map of traffic ratios (key is serviceId)
     */
    private Map<String,ConfigureTrafficRatioBusEvent.Ratio> ratios;

    /**
     * SimpleProxy properties - here you can map a context path and simply route to a specified host and port without
     * executing the filter chain and load balancing with ribbon.  This is primarily used for providing a route to
     * the docker API in node-proxy mode.
     */
    private SimpleProxy simpleProxy;


    public boolean isEdgeProxyMode() {
        return edgeProxyMode;
    }

    public RouterProperties setEdgeProxyMode(Boolean edgeProxyMode) {
        this.edgeProxyMode = edgeProxyMode;
        return this;
    }

    public void setEdgeProxyMode(boolean edgeProxyMode) {
        this.edgeProxyMode = edgeProxyMode;
    }

    public Map<String, Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, Route> routes) {
        this.routes = routes;
    }

    public Map<String, ConfigureTrafficRatioBusEvent.Ratio> getRatios() {
        return ratios;
    }

    public RouterProperties setRatios(Map<String, ConfigureTrafficRatioBusEvent.Ratio> ratios) {
        this.ratios = ratios;
        return this;
    }

    public SimpleProxy getSimpleProxy() {
        return simpleProxy;
    }

    public RouterProperties setSimpleProxy(SimpleProxy simpleProxy) {
        this.simpleProxy = simpleProxy;
        return this;
    }

    public static class Route {
        /**
         * Host header used for matching the route.  The filter will use the host header to determine which service to
         * route to.
         */
        private String vHost;

        /**
         * Default (minimum) compatable version. This is tagged in the context if the request path does not contain
         * version info. To specify a version of a request, prefix the path with /_/MAJ/MIN/PATCH/_/ - for example:
         * http://hello-world.cooksys.com/_/1/0/2/_/hello
         */
        private String defaultVersion;

        /**
         * Proxy route definitions for node proxy.
         */
        private Map<String,String> proxyRoutes;

        public String getvHost() {
            return vHost;
        }

        public void setvHost(String vHost) {
            this.vHost = vHost;
        }

        public String getDefaultVersion() {
            return defaultVersion;
        }

        public void setDefaultVersion(String defaultVersion) {
            this.defaultVersion = defaultVersion;
        }

        public Map<String, String> getProxyRoutes() {
            return proxyRoutes;
        }

        public void setProxyRoutes(Map<String, String> proxyRoutes) {
            this.proxyRoutes = proxyRoutes;
        }
    }

    public static class SimpleProxy {
        private boolean enabled=false;
        private Map<String,SimpleRoute> routes;

        public boolean isEnabled() {
            return enabled;
        }

        public SimpleProxy setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Map<String, SimpleRoute> getRoutes() {
            return routes;
        }

        public SimpleProxy setRoutes(Map<String, SimpleRoute> routes) {
            this.routes = routes;
            return this;
        }

        public static class SimpleRoute {
            private String contextPath;
            private String proxyHost;
            private Integer proxyPort;

            public String getContextPath() {
                return contextPath;
            }

            public SimpleRoute setContextPath(String contextPath) {
                this.contextPath = contextPath;
                return this;
            }

            public String getProxyHost() {
                return proxyHost;
            }

            public SimpleRoute setProxyHost(String proxyHost) {
                this.proxyHost = proxyHost;
                return this;
            }

            public Integer getProxyPort() {
                return proxyPort;
            }

            public SimpleRoute setProxyPort(Integer proxyPort) {
                this.proxyPort = proxyPort;
                return this;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("contextPath", contextPath)
                        .add("proxyHost", proxyHost)
                        .add("proxyPort", proxyPort)
                        .toString();
            }
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("enabled", enabled)
                    .add("routes", routes)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("edgeProxyMode", edgeProxyMode)
                .add("routes", routes)
                .add("ratios", ratios)
                .add("simpleProxy", simpleProxy)
                .toString();
    }
}
