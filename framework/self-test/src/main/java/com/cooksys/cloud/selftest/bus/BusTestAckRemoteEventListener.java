package com.cooksys.cloud.selftest.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bus.event.selftest.BusTestAckRemoteApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Listens for acknowlege events and updates the corresponding test adding the publisher's instanceId
 */
public class BusTestAckRemoteEventListener implements ApplicationListener<BusTestAckRemoteApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(BusTestAckRemoteEventListener.class);

    private BusTestService busTestService;

    public BusTestAckRemoteEventListener(BusTestService busTestService) {
        this.busTestService = busTestService;
    }

    @Override
    public void onApplicationEvent(BusTestAckRemoteApplicationEvent event) {
        logger.info("RX BusTestAckRemoteApplicationEvent: " + event);
        busTestService.storeBusTestAck(event.getUuid(),event.getInstanceId());
    }
}
