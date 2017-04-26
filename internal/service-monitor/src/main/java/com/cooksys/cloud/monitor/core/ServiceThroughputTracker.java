package com.cooksys.cloud.monitor.core;

import com.cooksys.cloud.commons.event.cloudmanager.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Temporary storage for service throughput metrics used by scale-down algorithm.  See {@link ServiceMetricEventListener}
 *
 * @author Tim Davidson
 */
public class ServiceThroughputTracker {
    private final Map<Service, ServiceThroughput> throughputMap = new HashMap<>();

    public boolean containsEntry(Service service) {
        return throughputMap.containsKey(service);
    }

    public boolean isTrackingThroughput(Service service) {
        if (!throughputMap.containsKey(service)) {
            return false;
        }

        return throughputMap.get(service).isTrackThroughput();
    }

    public void startTracking(Service service) {
        final ServiceThroughput serviceThroughput = new ServiceThroughput();
        serviceThroughput.setTrackThroughput(true);
        throughputMap.put(service, serviceThroughput);
    }

    public void stopTracking(Service service) {
        if (!throughputMap.containsKey(service) || !throughputMap.get(service).isTrackThroughput()) {
            throw new IllegalStateException("Service throughput is not currently being tracked");
        }

        final ServiceThroughput data = throughputMap.get(service);
        data.setTrackThroughput(false);
        data.setMaxThroughputPerInstance(data.getThroughput() / data.getNumInstances());
    }

    public void setThroughput(Service service, long throughput) {
        if (!throughputMap.containsKey(service) || !throughputMap.get(service).isTrackThroughput()) {
            throw new IllegalStateException("Service throughput is not currently being tracked");
        }
        throughputMap.get(service).setThroughput(throughput);
    }

    public void setInstances(Service service, int instances) {
        if (throughputMap.containsKey(service)) {
            throughputMap.get(service).setNumInstances(instances);
        }
    }

    public long getThroughput(Service service) {
        if (!throughputMap.containsKey(service)) {
            throw new IllegalStateException("service throughput not available");
        }
        return throughputMap.get(service).getThroughput();
    }

    public int getInstanceCount(Service service) {
        if (!throughputMap.containsKey(service)) {
            throw new IllegalStateException("service count not available");
        }
        return throughputMap.get(service).getNumInstances();
    }

    public long getMaxThroughputPerInstance(Service service) {
        if (!throughputMap.containsKey(service)) {
            throw new IllegalStateException("service count not available");
        }
        return throughputMap.get(service).getMaxThroughputPerInstance();
    }

    public Map<Service, ServiceThroughput> getThroughputMapCopy() {
        return new HashMap<Service,ServiceThroughput>(throughputMap);
    }
}
