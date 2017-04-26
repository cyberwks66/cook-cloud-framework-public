package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaleupServiceBusEvent;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.springframework.context.ApplicationListener;

/**
 * Listener for bus event {@link ScaleupServiceBusEvent}
 * event that is published by service-monitor that tells cloud manager to start the scaleup sequence for a service
 *
 * @author Tim Davidson
 */
public class ScaleupServiceBusEventListener implements ApplicationListener<ScaleupServiceBusEvent> {
    private LeaderElectionManager electionManager;
    private ServiceInstanceScaler serviceInstanceScaler;

    public ScaleupServiceBusEventListener(LeaderElectionManager electionManager, ServiceInstanceScaler serviceInstanceScaler) {
        this.electionManager = electionManager;
        this.serviceInstanceScaler = serviceInstanceScaler;
    }

    @Override
    public void onApplicationEvent(ScaleupServiceBusEvent event) {
        if(electionManager.isLeader()) {
            serviceInstanceScaler.scaleService(event.getServiceId(),event.getVersion(),event.getInstances());
        }
    }
}
