package com.cooksys.cloud.monitor.core;

import com.google.common.base.MoreObjects;

/**
 * Model class used to track service througput metrics
 *
 * @author Tim Davidson
 */
public class ServiceThroughput {
    private boolean trackThroughput;
    private int numInstances;
    private long throughput;
    private long maxThroughputPerInstance;

    public boolean isTrackThroughput() {
        return trackThroughput;
    }

    public ServiceThroughput setTrackThroughput(boolean trackThroughput) {
        this.trackThroughput = trackThroughput;
        return this;
    }

    public int getNumInstances() {
        return numInstances;
    }

    public ServiceThroughput setNumInstances(int numInstances) {
        this.numInstances = numInstances;
        return this;
    }

    public long getThroughput() {
        return throughput;
    }

    public ServiceThroughput setThroughput(long throughput) {
        this.throughput = throughput;
        return this;
    }

    public long getMaxThroughputPerInstance() {
        return maxThroughputPerInstance;
    }

    public ServiceThroughput setMaxThroughputPerInstance(long maxThroughputPerInstance) {
        this.maxThroughputPerInstance = maxThroughputPerInstance;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackThroughput", trackThroughput)
                .add("numInstances", numInstances)
                .add("throughput", throughput)
                .add("maxThroughputPerInstance", maxThroughputPerInstance)
                .toString();
    }
}
