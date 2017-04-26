package com.cooksys.cloud.selftest.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bus.event.selftest.BusTestRemoteApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Listener for BusTestRemoteApplicationEvent - simply publishes an ack event with its own instanceId
 */
public class BusTestRemoteEventListener implements ApplicationListener<BusTestRemoteApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(BusTestRemoteEventListener.class);

    private BusTestService busTestService;
    private DiscoveryClientService discoveryClientService;

    private String instanceId;

    public BusTestRemoteEventListener(BusTestService busTestService, String instanceId) {
        this.busTestService = busTestService;
        this.discoveryClientService=discoveryClientService;
        this.instanceId=instanceId;
    }

    @Override
    public void onApplicationEvent(BusTestRemoteApplicationEvent event) {
        logger.info("RX BusTestRemoteApplicationEvent: " + event);
        busTestService.sendAcknowledgeEvent(event.getUuid(),instanceId);
    }
}
