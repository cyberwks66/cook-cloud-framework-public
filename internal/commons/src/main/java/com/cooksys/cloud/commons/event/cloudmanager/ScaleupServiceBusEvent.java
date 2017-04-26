package com.cooksys.cloud.commons.event.cloudmanager;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;

/**
 * Event that is published by service-monitor when error percentage is below threshold - consumed by cloud-manager
 *
 * @author Tim Davidson
 */
public class ScaleupServiceBusEvent extends BusEvent {

    private String serviceId;
    private String version;
    private Integer instances;

    public ScaleupServiceBusEvent() {

    }

    public ScaleupServiceBusEvent(Object source, String serviceId, String version, Integer instances) {
        super(source);
        this.serviceId = serviceId;
        this.version = version;
        this.instances = instances;
    }

    public String getServiceId() {
        return serviceId;
    }

    public ScaleupServiceBusEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ScaleupServiceBusEvent setVersion(String version) {
        this.version = version;
        return this;
    }

    public Integer getInstances() {
        return instances;
    }

    public ScaleupServiceBusEvent setInstances(Integer instances) {
        this.instances = instances;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .add("version", version)
                .add("instances", instances)
                .toString();
    }
}
