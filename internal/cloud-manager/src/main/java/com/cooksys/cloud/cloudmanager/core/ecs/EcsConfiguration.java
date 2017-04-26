package com.cooksys.cloud.cloudmanager.core.ecs;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean definitions for AWS implementation
 *
 * @author Tim Davidson
 */
@Configuration
public class EcsConfiguration {

    @Bean
    public AmazonCloudFormation amazonCloudFormation() {
        return AmazonCloudFormationClientBuilder.defaultClient();
    }

    @Bean
    public AmazonAutoScaling amazonAutoScaling() {
        return AmazonAutoScalingClientBuilder.defaultClient();
    }

    @Bean
    public AmazonEC2 amazonEC2() {
        return AmazonEC2ClientBuilder.defaultClient();
    }

    @Bean
    public AmazonECS amazonECS() {
        return AmazonECSClientBuilder.defaultClient();
    }

    @Bean
    public AwsSdkWrapper awsSdkWrapper(AmazonCloudFormation amazonCloudFormation,
                                       AmazonAutoScaling amazonAutoScaling,
                                       AmazonEC2 amazonEC2,
                                       AmazonECS amazonECS) {
        return new AwsSdkWrapperImpl(amazonCloudFormation, amazonAutoScaling, amazonECS, amazonEC2);
    }

}