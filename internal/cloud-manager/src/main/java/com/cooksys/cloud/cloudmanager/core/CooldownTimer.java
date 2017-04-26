package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.cloudmanager.configuration.CloudManagerProperties;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Cooldown timer that is started after a scale (up/down) event.  Puts the service/cluster in a cooldown state, where
 * scale up/down events are ignored during this period
 *
 * @author Tim Davidson
 */
public class CooldownTimer extends Timer {
    private static final Logger logger = LoggerFactory.getLogger(CooldownTimer.class);

    private ServiceStateManager serviceStateManager;
    private ServiceClusterStateManager serviceClusterStateManager;
    private ApplicationEventPublisher context;
    private CloudManagerProperties config;

    public CooldownTimer(ServiceStateManager serviceStateManager, ServiceClusterStateManager serviceClusterStateManager, ApplicationEventPublisher context, CloudManagerProperties config) {
        this.serviceStateManager = serviceStateManager;
        this.serviceClusterStateManager = serviceClusterStateManager;
        this.context = context;
        this.config = config;
    }

    public void startServiceCooldown(String serviceId, String version) {
        logger.info("starting cooldown timer for serviceId: " + serviceId + " version: " + version);
        serviceStateManager.updateServiceState(new Service(serviceId, version), ScaleState.COOLDOWN);

        schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("cooldown complete for serviceId: " + serviceId + " version: " + version + " setting state to NORMAL");
                serviceStateManager.updateServiceState(new Service(serviceId, version), ScaleState.NORMAL);
            }
        }, config.getServiceCooldownPeriod());
    }

    public void startServiceClusterCooldown() {
        logger.info("starting cooldown for service cluster");

        serviceClusterStateManager.updateServiceClusterState(ScaleState.COOLDOWN);
        schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO update serviceCluster state to NORMAL
                logger.info("cooldown complete for cluster");
                serviceClusterStateManager.updateServiceClusterState(ScaleState.NORMAL);
            }
        }, config.getServiceClusterCooldownPeriod());
    }
}
