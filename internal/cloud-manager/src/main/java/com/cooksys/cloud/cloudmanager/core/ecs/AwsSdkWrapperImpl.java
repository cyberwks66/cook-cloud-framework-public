package com.cooksys.cloud.cloudmanager.core.ecs;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.model.*;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link AwsSdkWrapper}
 *
 * @author Tim Davidson
 */
public class AwsSdkWrapperImpl implements AwsSdkWrapper {

    public static final String SCALEDOWN = "SCALEDOWN";
    private AmazonCloudFormation acf;
    private AmazonAutoScaling aas;
    private AmazonECS ecs;
    private AmazonEC2 ec2;

    @Value("${ecs.serviceCluster}")
    private String clusterIdentifier;

    public AwsSdkWrapperImpl(AmazonCloudFormation acf, AmazonAutoScaling aas, AmazonECS ecs, AmazonEC2 ec2) {
        this.acf = acf;
        this.aas = aas;
        this.ecs = ecs;
        this.ec2 = ec2;
    }

   /*
        AmazonCloudFormation
    */

    @Override
    public DescribeStacksResult acfDescribeStacks() {
        return acf.describeStacks();
    }

    /*
        AmazonAutoScaling
     */

    @Override
    public DescribeAutoScalingGroupsResult aasDescribeAutoScalingGroup(String autoScalingGroupName) {
        DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest();
        final List<String> groupNames = new ArrayList<>();
        groupNames.add(autoScalingGroupName);
        request.setAutoScalingGroupNames(groupNames);
        return aas.describeAutoScalingGroups(request);
    }

    @Override
    public void aasSetDesiredCapacity(String autoScalingGroupName, int desiredCapacity) {
        SetDesiredCapacityRequest request = new SetDesiredCapacityRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setDesiredCapacity(desiredCapacity);
        aas.setDesiredCapacity(request);
    }

    /*
        EC2 Container Servicce
     */

    @Override
    public ListContainerInstancesResult ecsListContainerInstances() {
        final ListContainerInstancesRequest request = new ListContainerInstancesRequest();
        request.setCluster(clusterIdentifier);
        return ecs.listContainerInstances(request);
    }

    @Override
    public DescribeContainerInstancesResult ecsDescribeContainerInstances(List<String> instanceArns) {
        final DescribeContainerInstancesRequest request = new DescribeContainerInstancesRequest();
        request.setCluster(clusterIdentifier);
        request.setContainerInstances(instanceArns);
        return ecs.describeContainerInstances(request);
    }

    @Override
    public void ecsStopTask(String taskArn) {
        final StopTaskRequest request = new StopTaskRequest();
        request.setCluster(clusterIdentifier);
        request.setTask(taskArn);
        request.setReason(SCALEDOWN);
        ecs.stopTask(request);
    }

    @Override
    public void ecsUpdateServiceDesiredCount(String serviceIdentifier, int desiredCount) {
        final UpdateServiceRequest request=new UpdateServiceRequest();
        request.setCluster(clusterIdentifier);
        request.setService(serviceIdentifier);
        request.setDesiredCount(desiredCount);
        ecs.updateService(request);
    }

    @Override
    public DescribeServicesResult ecsDescribeService(String serviceIdentifier) {
        final List<String> services = new ArrayList<>();
        services.add(serviceIdentifier);
        final DescribeServicesRequest request = new DescribeServicesRequest();
        request.setCluster(clusterIdentifier);
        request.setServices(services);
        return ecs.describeServices(request);
    }

    // EC2
    @Override
    public void ec2StopInstance(String instanceId) {
        final StopInstancesRequest request = new StopInstancesRequest();
        final List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        request.setInstanceIds(instanceIds);
        ec2.stopInstances(request);
    }

    @Override
    public String getClusterIdentifier() {
        return clusterIdentifier;
    }

    @Override
    public void setInstanceProtection(String autoScalingGroupName, String instanceId,boolean protectedFromScaleIn){
        final SetInstanceProtectionRequest request = new SetInstanceProtectionRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        final List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);
        request.setInstanceIds(instanceIds);
        request.setProtectedFromScaleIn(protectedFromScaleIn);
        aas.setInstanceProtection(request);
    }

    @Override
    public void setInstanceProtectionAll(String autoScalingGroupName,boolean protectedFromScaleIn){
        final DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult = aasDescribeAutoScalingGroup(autoScalingGroupName);
        final List<Instance> instances = describeAutoScalingGroupsResult.getAutoScalingGroups().get(0).getInstances();
        final List<String> instanceIds = new ArrayList<>();

        for(Instance instance : instances) {
            instanceIds.add(instance.getInstanceId());
        }

        final SetInstanceProtectionRequest request = new SetInstanceProtectionRequest();
        request.setAutoScalingGroupName(autoScalingGroupName);
        request.setInstanceIds(instanceIds);
        request.setProtectedFromScaleIn(protectedFromScaleIn);

        aas.setInstanceProtection(request);
    }
}
