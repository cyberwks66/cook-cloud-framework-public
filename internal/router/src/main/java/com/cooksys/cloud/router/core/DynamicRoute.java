package com.cooksys.cloud.router.core;

import com.github.zafarkhaja.semver.Version;
import com.google.common.base.MoreObjects;

import java.util.Map;

/**
 * Interface that defines a route for the filter chain
 *
 * @author Tim Davidson
 */
public class DynamicRoute {

    /**
     * Service id to route to
     */
    private String serviceId;

    /**
     * Used by router in edge mode to match the route
     */
    private String vHost;

    /**
     * Default minimum version to route to if service consumer does not specify a version
     */
    private Version defaultVersion;

    /**
     * Router in edge mode appends the path prefix to the url before routing to the node proxy,
     * then router in node proxy mode uses the entry to match the route and then strips the
     * path prefix before forwarding to the service
     *
     * Key: path prefix (i.e /hello-world/1/0/1)
     * Value: semantic version
     */
    private Map<String,Version> proxyRoutes;

    public DynamicRoute() {
    }

    public DynamicRoute(String serviceId, String vHost, Version defaultVersion, Map<String, Version> proxyRoutes) {
        this.serviceId = serviceId;
        this.vHost = vHost;
        this.defaultVersion = defaultVersion;
        this.proxyRoutes = proxyRoutes;
    }

    public String getServiceId() {
        return serviceId;
    }

    public DynamicRoute setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getvHost() {
        return vHost;
    }

    public DynamicRoute setvHost(String vHost) {
        this.vHost = vHost;
        return this;
    }

    public Version getDefaultVersion() {
        return defaultVersion;
    }

    public DynamicRoute setDefaultVersion(Version defaultVersion) {
        this.defaultVersion = defaultVersion;
        return this;
    }

    public Map<String, Version> getProxyRoutes() {
        return proxyRoutes;
    }

    public DynamicRoute setProxyRoutes(Map<String, Version> proxyRoutes) {
        this.proxyRoutes = proxyRoutes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicRoute that = (DynamicRoute) o;

        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        if (vHost != null ? !vHost.equals(that.vHost) : that.vHost != null) return false;
        if (defaultVersion != null ? !defaultVersion.equals(that.defaultVersion) : that.defaultVersion != null)
            return false;
        return proxyRoutes != null ? proxyRoutes.equals(that.proxyRoutes) : that.proxyRoutes == null;

    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (vHost != null ? vHost.hashCode() : 0);
        result = 31 * result + (defaultVersion != null ? defaultVersion.hashCode() : 0);
        result = 31 * result + (proxyRoutes != null ? proxyRoutes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .add("vHost", vHost)
                .add("defaultVersion", defaultVersion)
                .add("proxyRoutes", proxyRoutes)
                .toString();
    }
}
