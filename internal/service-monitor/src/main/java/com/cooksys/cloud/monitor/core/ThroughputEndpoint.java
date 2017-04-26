package com.cooksys.cloud.monitor.core;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Actuator endpoint for inspecting current service throughput metrics
 *
 * @author Tim Davidson
 */
public class ThroughputEndpoint extends AbstractEndpoint<Map<String,ServiceThroughput>> {

    private ServiceThroughputTracker tracker;

    public ThroughputEndpoint(ServiceThroughputTracker tracker) {
        super("throughput");
        this.tracker = tracker;
    }

    @Override
    public Map<String, ServiceThroughput> invoke() {
        return tracker.getThroughputMapCopy().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
    }
}
