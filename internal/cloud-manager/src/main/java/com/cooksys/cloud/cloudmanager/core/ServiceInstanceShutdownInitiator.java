package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.cloudmanager.core.event.KillServiceInstanceEvent;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import com.cooksys.cloud.commons.event.sdk.DecommissionServiceInstanceBusEvent;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that handles the complete shutdown sequence of a service.
 *
 * Publishes a {@link KillServiceInstanceEvent} in the context when requesting
 * to kill an instance.  Specific cloud implementations should extend the {@link KillServiceInstanceListener} class
 * as a spring managed bean to handle killing the service
 *
 *
 * Shutdown sequence
 * 1. Send an event to the service instance telling it to decommission itself
 * 2. Set the state of the service to SCALING
 * 3. Start a two minute timer to allow discovery caches to get in sync (routers will stop sending traffic to the service)
 * 4. Stop the task - {@link KillServiceInstanceListener} implementation is responsible for this
 * 5. Start cooldown timer
 * 6. Place the state back to normal
 *
 * @author Tim Davidson
 */
public class ServiceInstanceShutdownInitiator {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceShutdownInitiator.class);

    private EurekaClient discoveryClient;
    private ServiceStateManager serviceStateManager;
    private ApplicationEventPublisher context;

    public ServiceInstanceShutdownInitiator(EurekaClient discoveryClient, ServiceStateManager serviceStateManager, ApplicationEventPublisher context) {
        this.discoveryClient = discoveryClient;
        this.serviceStateManager = serviceStateManager;
        this.context = context;
    }

    public void initiateShutdown(String serviceId, String version) {
        final ScaleState scaleState = serviceStateManager.getServiceState(new Service(serviceId, version));
        if (!ScaleState.NORMAL.equals(scaleState)) {
            logger.info("ignoring request to scale down service " + serviceId + " version: " + version + " scaleState: " + scaleState);
            return;
        }

        final InstanceInfo scaleDownInstance;
        try {
            scaleDownInstance = this.getOldestServiceInstance(serviceId, version);
        } catch (RuntimeException e) {
            logger.error("Could not find the oldest instance from eureka", e);
            return;
        }

        logger.info("initating scaledown sequence for service instance: " + scaleDownInstance);
        serviceStateManager.updateServiceState(new Service(serviceId, version), ScaleState.SCALING);

        // Publish event to the service instance that tells it to decommission itself (register DOWN)
        final DecommissionServiceInstanceBusEvent event =
                new DecommissionServiceInstanceBusEvent(this, serviceId, scaleDownInstance.getInstanceId());
        context.publishEvent(event);

        startDiscoveryCacheSyncTimer(scaleDownInstance,serviceId,version);
    }


    private InstanceInfo getOldestServiceInstance(String serviceId, String version) {
        final List<InstanceInfo> instancesVersionMatch = new ArrayList<>();

        final List<InstanceInfo> instances = discoveryClient.getApplication(serviceId).getInstances();
        for (InstanceInfo instance : instances) {
            if (version.equals(instance.getMetadata().get("version"))) {
                instancesVersionMatch.add(instance);
            }
        }

        InstanceInfo oldestInstance = instancesVersionMatch.get(0);

        for (InstanceInfo instance : instancesVersionMatch) {
            if (instance.getLeaseInfo().getServiceUpTimestamp() < oldestInstance.getLeaseInfo().getServiceUpTimestamp()) {
                oldestInstance = instance;
            }
        }
        if (oldestInstance == null) {
            throw new RuntimeException("oldestInstance is null");
        }
        return oldestInstance;
    }

    private void startDiscoveryCacheSyncTimer(InstanceInfo scaleDownInstance, String serviceId, String version) {
        final Timer discoveryCacheSyncTimer = new Timer();
        discoveryCacheSyncTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                final KillServiceInstanceEvent event = new KillServiceInstanceEvent(this,scaleDownInstance,serviceId,version);
                context.publishEvent(event);
            }
        }, 120_000L);
    }


}
