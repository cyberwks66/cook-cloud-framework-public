package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaledownServiceBusEvent;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.springframework.context.ApplicationListener;

/**
 * Listener for {@link ScaledownServiceBusEvent} -
 * Bus event that is published by service-monitor that tells cloud-manager to start scale-down sequence for a servicce
 *
 * @author Tim Davidson
 */
public class ScaledownServiceBusEventListener  implements ApplicationListener<ScaledownServiceBusEvent> {

    private LeaderElectionManager electionManager;
    private ServiceInstanceShutdownInitiator shutdownInitiator;

    public ScaledownServiceBusEventListener(LeaderElectionManager electionManager, ServiceInstanceShutdownInitiator shutdownInitiator) {
        this.electionManager = electionManager;
        this.shutdownInitiator = shutdownInitiator;
    }

    @Override
    public void onApplicationEvent(ScaledownServiceBusEvent event) {
        if(electionManager.isLeader()) {
           shutdownInitiator.initiateShutdown(event.getServiceId(),event.getVersion());
        }
    }
}
