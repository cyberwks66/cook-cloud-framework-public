package com.cooksys.cloud.sdk.core;

import com.cooksys.cloud.commons.event.sdk.DecommissionServiceInstanceBusEvent;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.ApplicationListener;

/**
 * Listener for Decommission event that cloud manager publishes for graceful degradation of traffic in a scaledown -
 * sets the health indicator to "DOWN"state.
 * This will eventually lead to the Discovery client reporting a state of DOWN to eureka server, so that
 * router will eventually stop sending traffic so no transactions will be broken.
 *
 * @author Tim Davidson
 */
public class DecommissionServiceInstanceBusEventListener implements HealthIndicator, ApplicationListener<DecommissionServiceInstanceBusEvent>{

    private EurekaInstanceConfigBean eurekaInstanceProps;

    public DecommissionServiceInstanceBusEventListener(EurekaInstanceConfigBean eurekaInstanceProps) {
        this.eurekaInstanceProps = eurekaInstanceProps;
    }

    private boolean decommissionState = false;

    @Override
    public Health health() {
        if (decommissionState) {
            return Health.down().build();
        } else {
            return Health.up().build();
        }
    }

    @Override
    public void onApplicationEvent(DecommissionServiceInstanceBusEvent event) {
        if(event.getInstanceId().equals(eurekaInstanceProps.getInstanceId())) {
            this.decommissionState = true;
        }
    }
}
