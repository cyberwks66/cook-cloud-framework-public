package com.cooksys.cloud.cloudmanager.api;

import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Request model for scale service API
 *
 * @author Tim Davidson
 */
public class ServiceRequest {
    @NotEmpty
    private String serviceId;

    @NotEmpty
    private String version;

    @NotNull
    private Integer instances;

    public ServiceRequest() {
    }

    public String getServiceId() {
        return serviceId;
    }

    public ServiceRequest setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ServiceRequest setVersion(String version) {
        this.version = version;
        return this;
    }

    public Integer getInstances() {
        return instances;
    }

    public ServiceRequest setInstances(Integer instances) {
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
