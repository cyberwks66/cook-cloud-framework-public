package com.cooksys.cloud.monitor.event;

import com.google.common.base.MoreObjects;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is published by {@link com.cooksys.cloud.monitor.core.TurbineStreamListenerThread}.  Consumed by
 * {@link com.cooksys.cloud.monitor.core.ServiceMetricEventListener}
 *
 * @author Tim Davidson
 */
public class ServiceMetricEvent extends ApplicationEvent {
    private String serviceId;
    private String version;
    private long requestCount;
    private double errorPercentage;
    private int reportingHosts;

    public ServiceMetricEvent(Object source, String serviceId, String version, long requestCount, double errorPercentage, int reportingHosts) {
        super(source);
        this.serviceId = serviceId;
        this.version = version;
        this.requestCount = requestCount;
        this.errorPercentage = errorPercentage;
        this.reportingHosts = reportingHosts;
    }

    public String getServiceId() {
        return serviceId;
    }

    public ServiceMetricEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ServiceMetricEvent setVersion(String version) {
        this.version = version;
        return this;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public ServiceMetricEvent setRequestCount(long requestCount) {
        this.requestCount = requestCount;
        return this;
    }

    public double getErrorPercentage() {
        return errorPercentage;
    }

    public ServiceMetricEvent setErrorPercentage(double errorPercentage) {
        this.errorPercentage = errorPercentage;
        return this;
    }

    public int getReportingHosts() {
        return reportingHosts;
    }

    public ServiceMetricEvent setReportingHosts(int reportingHosts) {
        this.reportingHosts = reportingHosts;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("serviceId", serviceId)
                .add("version", version)
                .add("requestCount", requestCount)
                .add("errorPercentage", errorPercentage)
                .add("reportingHosts", reportingHosts)
                .toString();
    }
}
