package com.cooksys.cloud.selftest.bus;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to interact with discovery client
 */
public class DiscoveryClientService {
    private DiscoveryClient discoveryClient;

    private String applicationName;

    public DiscoveryClientService(DiscoveryClient discoveryClient, String applicationName) {
        this.discoveryClient = discoveryClient;
        this.applicationName = applicationName;
    }

    /**
     * Returns a list of self test instance IDs currently registered with eureka
     * @return Optional List of instance IDs
     */
    public Optional<List<String>> getAvailableSelftestInstanceIds() {
        Optional<List<String>> emptyList = Optional.empty();

        List<ServiceInstance> eurekaInstances = discoveryClient.getInstances(applicationName);


        if(eurekaInstances == null || eurekaInstances.isEmpty()) {
            return emptyList;
        }

        final List<String> instanceIds = new ArrayList<>();

        for(ServiceInstance instance : eurekaInstances) {
            if(instance instanceof EurekaDiscoveryClient.EurekaServiceInstance) {
                instanceIds.add(((EurekaDiscoveryClient.EurekaServiceInstance)instance).getInstanceInfo().getInstanceId());
            }
        }

        return Optional.of(instanceIds);
    }

}
