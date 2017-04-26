package com.cooksys.cloud.commons.event.sdk;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;

/**
 * Event that is published by cloud-manager to a specific service instance to tell it to register with eureka in
 * the "DOWN" state.  This is used for graceful scale down, and causes the edge router to stop sending traffic to
 * the instance.
 *
 * @author Tim Davidson
 */
public class DecommissionServiceInstanceBusEvent extends BusEvent {
    private String serviceId;
    private String instanceId;

    public DecommissionServiceInstanceBusEvent() {
    }

    public DecommissionServiceInstanceBusEvent(Object source, String serviceId, String instanceId) {
        super(source);
        this.serviceId = serviceId;
        this.instanceId = instanceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public DecommissionServiceInstanceBusEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public DecommissionServiceInstanceBusEvent setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .add("instanceId", instanceId)
                .toString();
    }
}
