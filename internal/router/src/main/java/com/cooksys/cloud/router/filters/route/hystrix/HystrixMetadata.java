package com.cooksys.cloud.router.filters.route.hystrix;

/**
 * Metadata that is passed to the {@link HttpClientHystrixCommand}
 *
 * @author Tim Davidson
 */
public class HystrixMetadata {
    private String serviceId;
    private String version;
    private String threadpoolGroup;

    public HystrixMetadata() {
    }

    public HystrixMetadata(String serviceId, String version, String threadpoolGroup) {
        this.serviceId = serviceId;
        this.version = version;
        this.threadpoolGroup = threadpoolGroup;
    }

    public String getServiceId() {
        return serviceId;
    }

    public HystrixMetadata setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public HystrixMetadata setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getThreadpoolGroup() {
        return threadpoolGroup;
    }

    public HystrixMetadata setThreadpoolGroup(String threadpoolGroup) {
        this.threadpoolGroup = threadpoolGroup;
        return this;
    }
}
