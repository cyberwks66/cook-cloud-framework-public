package com.cooksys.cloud.commons.event.router;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;

import java.util.Map;

/**
 * Event that is triggered when a new dynamic route is configured - consumed by router in edgeProxy and nodeProxy modes
 *
 * @author Tim Davidson
 */
public class ConfigureRouteBusEvent extends BusEvent {
    private String serviceId;
    private String vHost;
    private String defaultVersion;
    private Map<String,String> proxyRoutes;

    public ConfigureRouteBusEvent() {
    }

    public ConfigureRouteBusEvent(Object source, String serviceId, String vHost, String defaultVersion, Map<String, String> proxyRoutes) {
        super(source);
        this.serviceId = serviceId;
        this.vHost = vHost;
        this.defaultVersion = defaultVersion;
        this.proxyRoutes = proxyRoutes;
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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .add("vHost", vHost)
                .add("defaultVersion", defaultVersion)
                .add("proxyRoutes", proxyRoutes)
                .toString();
    }
}
