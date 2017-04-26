package com.cooksys.cloud.commons.event.cloudmanager;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;

import java.util.Map;

/**
 * Event that replicates service state information to peer cloud-manager instances
 *
 * @author Tim Davidson
 */
public class ServiceStateBusEvent extends BusEvent {
    private Map<String,ScaleState> serviceStateMap;

    public ServiceStateBusEvent() {
    }

    public ServiceStateBusEvent(Object source, Map<String, ScaleState> serviceStateMap) {
        super(source);
        this.serviceStateMap = serviceStateMap;
    }

    public Map<String, ScaleState> getServiceStateMap() {
        return serviceStateMap;
    }

    public ServiceStateBusEvent setServiceStateMap(Map<String, ScaleState> serviceStateMap) {
        this.serviceStateMap = serviceStateMap;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceStateMap", serviceStateMap)
                .toString();
    }

}
