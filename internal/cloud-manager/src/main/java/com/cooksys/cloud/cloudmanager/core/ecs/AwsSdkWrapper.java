package com.cooksys.cloud.cloudmanager.core.ecs;

import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.ecs.model.DescribeContainerInstancesResult;
import com.amazonaws.services.ecs.model.DescribeServicesResult;
import com.amazonaws.services.ecs.model.ListContainerInstancesResult;

import java.util.List;

/**
 * Wrapper around common AWS API tasks
 *
 * @author Tim Davidson
 */
public interface AwsSdkWrapper {
    DescribeStacksResult acfDescribeStacks();

    DescribeAutoScalingGroupsResult aasDescribeAutoScalingGroup(String autoScalingGroupName);

    void aasSetDesiredCapacity(String autoScalingGroupName, int desiredCapacity);

    ListContainerInstancesResult ecsListContainerInstances();

    DescribeContainerInstancesResult ecsDescribeContainerInstances(List<String> instanceArns);

    void ecsStopTask(String taskArn);

    void ecsUpdateServiceDesiredCount(String serviceIdentifier, int desiredCount);

    DescribeServicesResult ecsDescribeService(String serviceIdentifier);

    void ec2StopInstance(String instanceId);

    String getClusterIdentifier();

    void setInstanceProtection(String autoScalingGroupName, String instanceId, boolean protectedFromScaleIn);

    void setInstanceProtectionAll(String autoScalingGroupName, boolean protectedFromScaleIn);
}
