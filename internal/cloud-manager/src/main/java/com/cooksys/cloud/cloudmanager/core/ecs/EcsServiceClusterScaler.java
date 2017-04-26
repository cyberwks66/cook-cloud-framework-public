package com.cooksys.cloud.cloudmanager.core.ecs;

import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Output;
import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.ecs.model.DescribeContainerInstancesResult;
import com.amazonaws.services.ecs.model.ListContainerInstancesResult;
import com.cooksys.cloud.cloudmanager.core.ServiceClusterNode;
import com.cooksys.cloud.cloudmanager.core.ServiceClusterScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AWS Implementation of {@link ServiceClusterScaler}
 *
 * @author Tim Davidson
 */
public class EcsServiceClusterScaler implements ServiceClusterScaler {
    private static final Logger logger = LoggerFactory.getLogger(EcsServiceClusterScaler.class);

    public static final String ECS_CLUSTER_NAME = "EcsClusterName";
    public static final String ECS_INSTANCE_ASG_NAME = "EcsInstanceAsgName";

    private AwsSdkWrapper aws;
    private String clusterIdentifier;

    public EcsServiceClusterScaler(AwsSdkWrapper aws) {
        this.aws = aws;
    }

    @PostConstruct
    public void init() {
        clusterIdentifier = aws.getClusterIdentifier();
    }

    @Override
    public List<ServiceClusterNode> findUnusedClusterNodes() {
        final ListContainerInstancesResult listContainerInstancesResult = aws.ecsListContainerInstances();

        if (listContainerInstancesResult.getContainerInstanceArns() == null || listContainerInstancesResult.getContainerInstanceArns().isEmpty()) {
            return new ArrayList<>();
        }

        final DescribeContainerInstancesResult describeContainerInstancesResult =
                aws.ecsDescribeContainerInstances(listContainerInstancesResult.getContainerInstanceArns());

        final List<ServiceClusterNode> spareContainerInstances =
                describeContainerInstancesResult.getContainerInstances().stream()
                        .filter(containerInstance -> (
                                // router-node started and no pending tasks:
                                containerInstance.getRunningTasksCount().equals(1)
                                        && containerInstance.getPendingTasksCount().equals(0))
                                // router-node has not started yet:
                                || containerInstance.getRunningTasksCount().equals(0))
                        .map(containerInstance -> new Ec2Instance(containerInstance.getEc2InstanceId()))
                        .collect(Collectors.toList());

        return spareContainerInstances;
    }

    @Override
    public void stopClusterNode(ServiceClusterNode node) {
        if (!(node instanceof Ec2Instance)) {
            throw new IllegalArgumentException("Expected Ec2Instance, but got " + node.getClass().getName());
        }
        final Ec2Instance ec2Instance = (Ec2Instance) node;
        final String autoScalingGroup = getAutoScalingGroup();

        // here we will set autoscale protection on all instances in the group,
        // then turn off protection for a specific instance - this will cause that instance to
        // scale down when we decrement desired instances
        //
        // I had to use this method to scale down, because stopping the instance directly using ec2 would
        // cause autoscaler to spawn a new instance to replace it.  This metohd of setting autoscale protection
        // gives us more control and prevents any race condition that may occur when trying to stop an instance and
        // immediatly decrement the desired instance count, since this requires 2 api calls to different apis

        aws.setInstanceProtectionAll(autoScalingGroup, true);
        aws.setInstanceProtection(autoScalingGroup, ec2Instance.getInstanceId(), false);

        decrementClusterDesiredInstances();
    }

    @Override
    public void addClusterNode() {
        String autoScalingGroupName = getAutoScalingGroup();
        int currentDesiredCapacity = getCurrentDesiredCapacity(autoScalingGroupName);

        setDesiredCapacity(autoScalingGroupName, currentDesiredCapacity + 1);

        aws.ecsUpdateServiceDesiredCount("router-node", currentDesiredCapacity + 1);

    }

    @Override
    public int getCurrentClusterNodeCount() {
        return getCurrentCapacity(getAutoScalingGroup());
    }

    private void decrementClusterDesiredInstances() throws RuntimeException {
        final String autoScalingGroupName = getAutoScalingGroup();
        int currentDesiredCapacity = getCurrentDesiredCapacity(autoScalingGroupName);

        if (currentDesiredCapacity - 1 < 0) {
            setDesiredCapacity(autoScalingGroupName, 0);
            aws.ecsUpdateServiceDesiredCount("router-node", 0);

        } else {
            setDesiredCapacity(autoScalingGroupName, currentDesiredCapacity - 1);
            aws.ecsUpdateServiceDesiredCount("router-node", currentDesiredCapacity - 1);
        }
    }

    private String getAutoScalingGroup() {
        final DescribeStacksResult describeStacksResult = aws.acfDescribeStacks();

        if (describeStacksResult == null || describeStacksResult.getStacks() == null || describeStacksResult.getStacks().isEmpty()) {
            throw new RuntimeException("There are no CloudFormationStacks configured");
        }

        Stack matchedStack = null;
        for (Stack stack : describeStacksResult.getStacks()) {
            for (Parameter parameter : stack.getParameters()) {
                if (ECS_CLUSTER_NAME.equals(parameter.getParameterKey()) &&
                        clusterIdentifier.equals(parameter.getParameterValue())) {
                    matchedStack = stack;
                }
            }
        }

        if (matchedStack == null) {
            throw new RuntimeException("CloudFormationStack not found for EcsClusterName: " + clusterIdentifier);
        }

        String autoScalingGroupName = null;

        for (Output output : matchedStack.getOutputs()) {
            if (ECS_INSTANCE_ASG_NAME.equals(output.getOutputKey())) {
                autoScalingGroupName = output.getOutputValue();
            }
        }

        if (autoScalingGroupName == null) {
            throw new RuntimeException("AutoScaling group not configured for EcsClusterName: " + clusterIdentifier);
        }

        return autoScalingGroupName;
    }

    private int getCurrentDesiredCapacity(String autoScalingGroupName) {
        final DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult =
                aws.aasDescribeAutoScalingGroup(autoScalingGroupName);

        if (describeAutoScalingGroupsResult.getAutoScalingGroups() == null || describeAutoScalingGroupsResult.getAutoScalingGroups().isEmpty()) {
            throw new RuntimeException("Empty list returned for DescribeAutoScalingGroup groupName: " + autoScalingGroupName);
        }

        final Integer desiredCapacity = describeAutoScalingGroupsResult.getAutoScalingGroups().get(0).getDesiredCapacity();
        logger.info("desiredCapacity: " + desiredCapacity.toString());
        return desiredCapacity;
    }

    private void setDesiredCapacity(String autoScalingGroupName, int desiredCapacity) throws RuntimeException {
        aws.aasSetDesiredCapacity(autoScalingGroupName, desiredCapacity);
        logger.info("Set desiredCapacity: " + desiredCapacity + " for AutoScalingGroup: " + autoScalingGroupName);
    }

    private int getCurrentCapacity(String autoScalingGroupName) {
        final DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult =
                aws.aasDescribeAutoScalingGroup(autoScalingGroupName);

        if (describeAutoScalingGroupsResult.getAutoScalingGroups() == null
                || describeAutoScalingGroupsResult.getAutoScalingGroups().isEmpty()) {
            throw new RuntimeException("Empty list returned for DescribeAutoScalingGroup groupName: " + autoScalingGroupName);
        }

        if (describeAutoScalingGroupsResult.getAutoScalingGroups().get(0).getInstances() == null) {
            return 0;
        }

        return describeAutoScalingGroupsResult.getAutoScalingGroups().get(0).getInstances().size();
    }

    private void setScaleProtectionAllInstances(String autoScalingGroupName) {
        final DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult =
                aws.aasDescribeAutoScalingGroup(autoScalingGroupName);

        if (describeAutoScalingGroupsResult.getAutoScalingGroups() == null || describeAutoScalingGroupsResult.getAutoScalingGroups().isEmpty()) {
            throw new RuntimeException("Empty list returned for DescribeAutoScalingGroup groupName: " + autoScalingGroupName);
        }


    }

}
