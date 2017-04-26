package com.cooksys.cloud.cloudmanager.core.ecs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS-specific config properties
 *
 * @author Tim Davidson
 */
@ConfigurationProperties(prefix = "ecs")
public class EcsProperties {
    private String serviceCluster;

    private String edgeCluster;



    public String getServiceCluster() {
        return serviceCluster;
    }

    public EcsProperties setServiceCluster(String serviceCluster) {
        this.serviceCluster = serviceCluster;
        return this;
    }

    public String getEdgeCluster() {
        return edgeCluster;
    }

    public EcsProperties setEdgeCluster(String edgeCluster) {
        this.edgeCluster = edgeCluster;
        return this;
    }

}
