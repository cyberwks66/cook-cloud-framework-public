package com.cooksys.cloud.cloudmanager.core.ecs;

import com.amazonaws.services.ecs.model.DescribeServicesResult;
import com.cooksys.cloud.cloudmanager.core.CooldownTimer;
import com.cooksys.cloud.cloudmanager.core.KillServiceInstanceListener;
import com.cooksys.cloud.commons.SharedConstants;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Implementation of {@link KillServiceInstanceListener}
 *
 * @author Tim Davidson
 */
public class EcsKillServiceInstanceListener extends KillServiceInstanceListener {
    private static final Logger logger = LoggerFactory.getLogger(EcsKillServiceInstanceListener.class);

    private AwsSdkWrapper aws;

    public EcsKillServiceInstanceListener(CooldownTimer cooldownTimer, AwsSdkWrapper aws) {
        super(cooldownTimer);
        this.aws = aws;
    }

    @Override
    public void killServiceInstance(InstanceInfo instance, String serviceId, String version) {
        final String serviceIdentifier = EcsUtil.formatServiceIdentifier(serviceId, version);
        final String instanceArn = instance.getMetadata().get(SharedConstants.EUREKA_METAKEY_ECS_TASK_ARN);

        aws.ecsStopTask(instanceArn);

        // Lower the desired count, so ECS doesn't try to restart the task we just stopped
        final int currentDesiredCount = getCurrentDesiredCount(serviceIdentifier);

        aws.ecsUpdateServiceDesiredCount(
                serviceIdentifier, currentDesiredCount - 1);

    }

    protected int getCurrentDesiredCount(String serviceIdentifier) throws RuntimeException {
        final DescribeServicesResult describeServicesResult = aws.ecsDescribeService( serviceIdentifier);

        if (describeServicesResult == null || describeServicesResult.getServices() == null || describeServicesResult.getServices().get(0) == null) {
            throw new RuntimeException("Service: " + serviceIdentifier +" not found");
        }

        final int currentDesiredCount = describeServicesResult.getServices().get(0).getDesiredCount();
        logger.info("currentDesiredCount: " + currentDesiredCount);

        return currentDesiredCount;
    }
}
