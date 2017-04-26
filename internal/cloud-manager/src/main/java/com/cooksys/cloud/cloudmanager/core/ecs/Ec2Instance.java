package com.cooksys.cloud.cloudmanager.core.ecs;

import com.cooksys.cloud.cloudmanager.core.ServiceClusterNode;
import com.google.common.base.MoreObjects;

/**
 * EC2 Implementaion of {@link ServiceClusterNode}
 *
 * @author Tim Davidson
 */
public class Ec2Instance implements ServiceClusterNode {
    private String instanceId;

    public Ec2Instance() {
    }

    public Ec2Instance(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Ec2Instance setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instanceId", instanceId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ec2Instance that = (Ec2Instance) o;

        return instanceId != null ? instanceId.equals(that.instanceId) : that.instanceId == null;
    }

    @Override
    public int hashCode() {
        return instanceId != null ? instanceId.hashCode() : 0;
    }
}
