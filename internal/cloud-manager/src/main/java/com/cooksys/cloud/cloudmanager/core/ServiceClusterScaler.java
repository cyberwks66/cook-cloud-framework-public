package com.cooksys.cloud.cloudmanager.core;

import java.util.List;

/**
 * Interface for cloud-specific implementations to perform various tasks to the cluster
 *
 * @author Tim Davidson
 */
public interface ServiceClusterScaler {
    List<ServiceClusterNode> findUnusedClusterNodes();

    void stopClusterNode(ServiceClusterNode node);

    void addClusterNode();

    int getCurrentClusterNodeCount();
}
