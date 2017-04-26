package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.ServiceClusterStateBusEvent;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

/**
 * Event listener for {@link ServiceClusterStateBusEvent}
 *
 * @author Tim Davidson
 */
public class ServiceClusterStateManager implements ApplicationListener<ServiceClusterStateBusEvent> {
    private LeaderElectionManager electionManager;
    private ApplicationEventPublisher context;

    private ScaleState clusterState = ScaleState.NORMAL;

    public ServiceClusterStateManager(LeaderElectionManager electionManager, ApplicationEventPublisher context) {
        this.electionManager = electionManager;
        this.context = context;

    }

    public void updateServiceClusterState(ScaleState scaleState) {
        this.clusterState = scaleState;
        context.publishEvent(new ServiceClusterStateBusEvent(this, scaleState));
    }

    @Override
    public void onApplicationEvent(ServiceClusterStateBusEvent event) {
        if (!electionManager.isLeader()) {
            this.clusterState = event.getClusterState();
        }
    }

    public ScaleState getClusterState() {
        return clusterState;
    }

}
