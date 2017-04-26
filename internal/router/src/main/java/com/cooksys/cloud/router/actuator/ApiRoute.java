package com.cooksys.cloud.router.actuator;


import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

/**
 * Model object for Routes API requests and response body
 *
 * @author Tim Davidson
 */
public class ApiRoute {
    private String link;

    @NotBlank
    private String serviceId;

    @NotBlank
    private String vHost;

    @NotBlank
    private String defaultVersion;

    @NotEmpty
    private Map<String, String> proxyRoutes;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiRoute apiRoute = (ApiRoute) o;

        if (link != null ? !link.equals(apiRoute.link) : apiRoute.link != null) return false;
        if (serviceId != null ? !serviceId.equals(apiRoute.serviceId) : apiRoute.serviceId != null) return false;
        if (vHost != null ? !vHost.equals(apiRoute.vHost) : apiRoute.vHost != null) return false;
        if (defaultVersion != null ? !defaultVersion.equals(apiRoute.defaultVersion) : apiRoute.defaultVersion != null)
            return false;
        return proxyRoutes != null ? proxyRoutes.equals(apiRoute.proxyRoutes) : apiRoute.proxyRoutes == null;

    }

    @Override
    public int hashCode() {
        int result = link != null ? link.hashCode() : 0;
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (vHost != null ? vHost.hashCode() : 0);
        result = 31 * result + (defaultVersion != null ? defaultVersion.hashCode() : 0);
        result = 31 * result + (proxyRoutes != null ? proxyRoutes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("link", link)
                .add("serviceId", serviceId)
                .add("vHost", vHost)
                .add("defaultVersion", defaultVersion)
                .add("proxyRoutes", proxyRoutes)
                .toString();
    }
}
