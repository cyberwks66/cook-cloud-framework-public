package com.cooksys.cloud.cloudmanager.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for cloud-manager
 *
 * @author Tim Davidson
 */
@ConfigurationProperties(prefix = "cloudManager")
public class CloudManagerProperties {
    private long serviceCooldownPeriod = 30_000;
    private long serviceClusterCooldownPeriod = 120_000;
    private int clusterNodeReserveCount = 1;

    public long getServiceCooldownPeriod() {
        return serviceCooldownPeriod;
    }

    public CloudManagerProperties setServiceCooldownPeriod(long serviceCooldownPeriod) {
        this.serviceCooldownPeriod = serviceCooldownPeriod;
        return this;
    }

    public long getServiceClusterCooldownPeriod() {
        return serviceClusterCooldownPeriod;
    }

    public CloudManagerProperties setServiceClusterCooldownPeriod(long serviceClusterCooldownPeriod) {
        this.serviceClusterCooldownPeriod = serviceClusterCooldownPeriod;
        return this;
    }

    public int getClusterNodeReserveCount() {
        return clusterNodeReserveCount;
    }

    public CloudManagerProperties setClusterNodeReserveCount(int clusterNodeReserveCount) {
        this.clusterNodeReserveCount = clusterNodeReserveCount;
        return this;
    }
}
