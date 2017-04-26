package com.cooksys.cloud.cloudmanager.core.event;

import com.google.common.base.MoreObjects;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is triggered by the abstract scaling logic - cloud-specific implementations should handle this event
 * by sub-classing {@link com.cooksys.cloud.cloudmanager.core.KillServiceInstanceListener}
 *
 * @author Tim Davidson
 */
public class KillServiceInstanceEvent extends ApplicationEvent{
    private InstanceInfo serviceInstance;
    private String serviceId;
    private String version;

    public KillServiceInstanceEvent(Object source, InstanceInfo serviceInstance, String serviceId, String version) {
        super(source);
        this.serviceInstance = serviceInstance;
        this.serviceId = serviceId;
        this.version = version;
    }

    public InstanceInfo getServiceInstance() {
        return serviceInstance;
    }

    public KillServiceInstanceEvent setServiceInstance(InstanceInfo serviceInstance) {
        this.serviceInstance = serviceInstance;
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public KillServiceInstanceEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public KillServiceInstanceEvent setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceInstance", serviceInstance)
                .add("serviceId", serviceId)
                .add("version", version)
                .toString();
    }
}
