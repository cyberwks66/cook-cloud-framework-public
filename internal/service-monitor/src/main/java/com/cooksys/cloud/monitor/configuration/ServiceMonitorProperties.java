package com.cooksys.cloud.monitor.configuration;

/**
 * Config properties for service-monitor application
 *
 * @author Tim Davidson
 */
public class ServiceMonitorProperties {
    /**
     * Specifies the error percentage threshold for triggering over threshold scenario
     */
    private int errorPercentageThreshold=3;

    /**
     * specifies how many over threshold events that will trigger scale up of a service.
     * note: under threshold events will decrement the counter
     */
    private int thresholdBreachedCounterMax=10;

    /**
     * Percentage of tracked throughput that will trigger a scale-down event
     */
    private int scaledownThroughputPercentage=70;


    public int getErrorPercentageThreshold() {
        return errorPercentageThreshold;
    }

    public ServiceMonitorProperties setErrorPercentageThreshold(int errorPercentageThreshold) {
        this.errorPercentageThreshold = errorPercentageThreshold;
        return this;
    }

    public int getThresholdBreachedCounterMax() {
        return thresholdBreachedCounterMax;
    }

    public ServiceMonitorProperties setThresholdBreachedCounterMax(int thresholdBreachedCounterMax) {
        this.thresholdBreachedCounterMax = thresholdBreachedCounterMax;
        return this;
    }

    public int getScaledownThroughputPercentage() {
        return scaledownThroughputPercentage;
    }

    public ServiceMonitorProperties setScaledownThroughputPercentage(int scaledownThroughputPercentage) {
        this.scaledownThroughputPercentage = scaledownThroughputPercentage;
        return this;
    }
}
