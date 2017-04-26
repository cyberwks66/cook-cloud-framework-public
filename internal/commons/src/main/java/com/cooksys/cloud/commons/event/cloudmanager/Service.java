package com.cooksys.cloud.commons.event.cloudmanager;

import com.google.common.base.MoreObjects;

/**
 * Created by timd on 4/11/17.
 */
public class Service {
    private String serviceId;
    private String version;

    public Service(String serviceId, String version) {
        this.serviceId = serviceId;
        this.version = version;
    }

    public static Service fromServiceDescriptor(String serviceDescriptor) {
        final String[] splitServiceDescriptor = serviceDescriptor.split(":");
        if(splitServiceDescriptor == null || splitServiceDescriptor.length != 2) {
            throw new IllegalArgumentException("malformed service descriptor - format should be <serviceId>:<version>");
        }
        return new Service(splitServiceDescriptor[0],splitServiceDescriptor[1]);
    }

    public static String toServiceDescriptor(Service service) {
        return service.getServiceId() + ":" + service.getVersion();
    }

    public String getServiceId() {
        return serviceId;
    }

    public Service setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Service setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Service service = (Service) o;

        if (!serviceId.equals(service.serviceId)) return false;
        return version.equals(service.version);

    }

    @Override
    public int hashCode() {
        int result = serviceId.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .add("version", version)
                .toString();
    }
}