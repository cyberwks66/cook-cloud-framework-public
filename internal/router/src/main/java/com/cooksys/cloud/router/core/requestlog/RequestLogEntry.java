package com.cooksys.cloud.router.core.requestlog;

import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * Model class for elasticsearch request logging event
 *
 * @author Tim Davidson
 */
public class RequestLogEntry {
    private Date timestamp;
    private String eventName;
    private String requestHostHeader;
    private String requestPath;
    private String requestMethod;
    private String serviceId;
    private String routeVersion;
    private String responseCode;
    private boolean error;
    private String errorMessage;
    private long responseTime;

    public RequestLogEntry() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public RequestLogEntry setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public RequestLogEntry setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getRequestHostHeader() {
        return requestHostHeader;
    }

    public RequestLogEntry setRequestHostHeader(String requestHostHeader) {
        this.requestHostHeader = requestHostHeader;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public RequestLogEntry setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public RequestLogEntry setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public String getServiceId() {
        return serviceId;
    }

    public RequestLogEntry setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getRouteVersion() {
        return routeVersion;
    }

    public RequestLogEntry setRouteVersion(String routeVersion) {
        this.routeVersion = routeVersion;
        return this;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public RequestLogEntry setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public boolean isError() {
        return error;
    }

    public RequestLogEntry setError(boolean error) {
        this.error = error;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public RequestLogEntry setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public RequestLogEntry setResponseTime(long responseTime) {
        this.responseTime = responseTime;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp",timestamp)
                .add("eventName", eventName)
                .add("requestHostHeader", requestHostHeader)
                .add("requestPath", requestPath)
                .add("requestMethod", requestMethod)
                .add("routeVersion", routeVersion)
                .add("responseCode", responseCode)
                .add("error", error)
                .add("errorMessage", errorMessage)
                .add("responseTime", responseTime)
                .toString();
    }
}
