package com.cooksys.cloud.commons.event.cloudmanager;

import com.cooksys.cloud.commons.event.BusEvent;
import com.google.common.base.MoreObjects;

/**
 * Event for replicating the cluster state information to peer instances of cloud-manager
 *
 * @author Tim Davidson
 */
public class ServiceClusterStateBusEvent extends BusEvent {
    private ScaleState clusterState;

    public ServiceClusterStateBusEvent() {
    }

    public ServiceClusterStateBusEvent(Object source, ScaleState clusterState) {
        super(source);
        this.clusterState = clusterState;
    }

    public ScaleState getClusterState() {
        return clusterState;
    }

    public ServiceClusterStateBusEvent setClusterState(ScaleState clusterState) {
        this.clusterState = clusterState;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clusterState", clusterState)
                .toString();
    }
}
