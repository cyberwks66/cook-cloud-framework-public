package com.cooksys.cloud.commons.event.router;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;

/**
 * Event that is triggered when a Route is deleted - consumed by router
 *
 * @author Tim Davidson
 */
public class RemoveRouteBusEvent extends BusEvent {
    private String serviceId;

    public RemoveRouteBusEvent() {}

    public RemoveRouteBusEvent(Object source, String serviceId) {
        super(source);
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public RemoveRouteBusEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .toString();
    }
}
