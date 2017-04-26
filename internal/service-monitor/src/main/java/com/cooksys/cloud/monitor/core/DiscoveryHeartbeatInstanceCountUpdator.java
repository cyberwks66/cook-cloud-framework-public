package com.cooksys.cloud.monitor.core;

import com.cooksys.cloud.commons.SharedConstants;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for {@link HeartbeatEvent}
 * Updates {@link ServiceThroughputTracker} with instance counts of each unique serviceId/version combination
 *
 * @author Tim Davidson
 */
public class DiscoveryHeartbeatInstanceCountUpdator implements ApplicationListener<HeartbeatEvent> {

    private final ServiceThroughputTracker serviceThroughputTracker;
    private final DiscoveryClient discoveryClient;

    public DiscoveryHeartbeatInstanceCountUpdator(ServiceThroughputTracker serviceThroughputTracker, DiscoveryClient discoveryClient) {
        this.serviceThroughputTracker = serviceThroughputTracker;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void onApplicationEvent(HeartbeatEvent event) {
        final Applications applications = discoveryClient.getApplications();
        for(Application application : applications.getRegisteredApplications()) {
            final Map<String,Integer> versionToIntegerMap = new HashMap<>();

            for(InstanceInfo instance : application.getInstances()) {
                final String version = instance.getMetadata().get(SharedConstants.EUREKA_METAKEY_VERSION);
                if(version !=null) {
                    if(!versionToIntegerMap.containsKey(version)) {
                        versionToIntegerMap.put(version,1);
                    } else {
                        versionToIntegerMap.put(version,versionToIntegerMap.get(version) +1);
                    }
                }
            }

            for(Map.Entry<String,Integer> entry : versionToIntegerMap.entrySet()) {
                serviceThroughputTracker.setInstances(
                        new Service(application.getName().toLowerCase(),entry.getKey()),entry.getValue());
            }
        }
    }
}
