package com.cooksys.cloud.cloudmanager.core.ecs;

import com.amazonaws.services.ecs.model.DescribeServicesResult;
import com.cooksys.cloud.cloudmanager.core.CooldownTimer;
import com.cooksys.cloud.cloudmanager.core.ServiceInstanceScaler;
import com.cooksys.cloud.cloudmanager.core.ServiceStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Amazon EC2 Container Service implementation of {@link ServiceInstanceScaler}
 *
 * @author Tim Davidson
 */
public class EcsServiceInstanceScaler extends ServiceInstanceScaler {
    private static final Logger logger = LoggerFactory.getLogger(EcsServiceInstanceScaler.class);

    private AwsSdkWrapper aws;

    public EcsServiceInstanceScaler(ServiceStateManager serviceStateManager, CooldownTimer cooldownTimer, AwsSdkWrapper aws) {
        super(serviceStateManager, cooldownTimer);
        this.aws = aws;
    }

    @Override
    public void scale(String serviceId, String version, Integer instances) {
        final String serviceIdentifier = EcsUtil.formatServiceIdentifier(serviceId, version);

        final int currentServiceInstanceCount = getCurrentRunningCount(serviceIdentifier);
        final int serviceDesiredCount = currentServiceInstanceCount + instances;

        logger.debug("currentServiceInstanceCount: " + currentServiceInstanceCount);
        logger.debug("serviceDesiredCount: " + serviceDesiredCount);

        logger.info("scaling up service - serviceIdentifier: " + serviceIdentifier + " instances: " + instances);

        setDesiredCount(serviceIdentifier, serviceDesiredCount);
    }

    @Override
    protected boolean isScaleComplete(String serviceId, String version) {
        final String serviceIdentifier = EcsUtil.formatServiceIdentifier(serviceId, version);
        final int desiredCount = getCurrentDesiredCount(serviceIdentifier);
        final int currentCount = getCurrentRunningCount(serviceIdentifier);

        return desiredCount==currentCount;
    }

    private int getCurrentDesiredCount(String serviceIdentifier) throws RuntimeException {
        final DescribeServicesResult describeServicesResult = aws.ecsDescribeService(serviceIdentifier);

        if (describeServicesResult == null || describeServicesResult.getServices() == null || describeServicesResult.getServices().get(0) == null) {
            throw new RuntimeException("Service: " + serviceIdentifier + " not found");
        }

        final int currentDesiredCount = describeServicesResult.getServices().get(0).getDesiredCount();
        logger.info("currentDesiredCount: " + currentDesiredCount);

        return currentDesiredCount;
    }

    private int getCurrentRunningCount(String serviceIdentifier) {
        final DescribeServicesResult describeServicesResult = aws.ecsDescribeService(serviceIdentifier);

        if (describeServicesResult == null || describeServicesResult.getServices() == null || describeServicesResult.getServices().get(0) == null) {
            throw new RuntimeException("Service: " + serviceIdentifier +  " not found");
        }

        return describeServicesResult.getServices().get(0).getRunningCount();
    }

    private void setDesiredCount(String serviceIdentifier, int desiredCount) {
        aws.ecsUpdateServiceDesiredCount(serviceIdentifier, desiredCount);
        logger.info("updated desiredCount: " + desiredCount);
    }
}
