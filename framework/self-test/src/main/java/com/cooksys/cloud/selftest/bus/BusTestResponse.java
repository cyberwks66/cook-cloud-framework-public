package com.cooksys.cloud.selftest.bus;

import java.util.List;

/**
 * JSON Response of the POST /busTest API
 */
public class BusTestResponse {
    private String testId;
    private List<String> discoverySelftestInstances;

    public List<String> getDiscoverySelftestInstances() {
        return discoverySelftestInstances;
    }

    public BusTestResponse setDiscoverySelftestInstances(List<String> discoverySelftestInstances) {
        this.discoverySelftestInstances = discoverySelftestInstances;
        return this;
    }

    public String getTestId() {
        return testId;
    }

    public BusTestResponse setTestId(String testId) {
        this.testId = testId;
        return this;
    }
}
