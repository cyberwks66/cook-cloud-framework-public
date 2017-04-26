package com.cooksys.cloud.cloudmanager.api;

import com.google.common.base.MoreObjects;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Request model for scale cluster API
 *
 * @author Tim Davidson
 */
public class ClusterRequest {

    @NotEmpty
    private String clusterIdentifier;

    @NotNull
    private Integer instances;

    public ClusterRequest() {
    }

    public ClusterRequest(String clusterIdentifier, Integer instances) {
        this.clusterIdentifier = clusterIdentifier;
        this.instances = instances;
    }

    public String getClusterIdentifier() {
        return clusterIdentifier;
    }

    public ClusterRequest setClusterIdentifier(String clusterIdentifier) {
        this.clusterIdentifier = clusterIdentifier;
        return this;
    }

    public Integer getInstances() {
        return instances;
    }

    public ClusterRequest setInstances(Integer instances) {
        this.instances = instances;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterRequest that = (ClusterRequest) o;

        if (clusterIdentifier != null ? !clusterIdentifier.equals(that.clusterIdentifier) : that.clusterIdentifier != null)
            return false;
        return instances != null ? instances.equals(that.instances) : that.instances == null;

    }

    @Override
    public int hashCode() {
        int result = clusterIdentifier != null ? clusterIdentifier.hashCode() : 0;
        result = 31 * result + (instances != null ? instances.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clusterIdentifier", clusterIdentifier)
                .add("instances", instances)
                .toString();
    }
}
