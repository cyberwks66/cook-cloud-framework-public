package com.cooksys.cloud.cloudmanager.configuration;

import com.cooksys.cloud.cloudmanager.core.*;
import com.cooksys.cloud.cloudmanager.core.ecs.*;
import com.cooksys.cloud.commons.event.EventConfiguration;
import com.cooksys.cloud.commons.event.cloudmanager.ScaledownServiceBusEvent;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleupServiceBusEvent;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import com.netflix.discovery.EurekaClient;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring bean configuration for cloud-manager
 *
 * @author Tim Davidson
 */
@Configuration
@Import(EventConfiguration.class)
public class CloudManagerConfiguration {

    @Bean
    public CloudManagerProperties cloudManagerProperties() {
        return new CloudManagerProperties();
    }

    @Bean
    public EcsProperties ecsProperties() {
        return new EcsProperties();
    }


    @Bean
    public ServiceStateManager serviceStateManager(LeaderElectionManager electionManager,
                                                   ApplicationEventPublisher context) {
        return new ServiceStateManager(electionManager, context);
    }

    @Bean
    public ServiceClusterStateManager serviceClusterStateManager(LeaderElectionManager electionManager,
                                                                 ApplicationEventPublisher applicationEventPublisher) {
        return new ServiceClusterStateManager(electionManager, applicationEventPublisher);
    }

    @Bean
    public ApplicationListener<ScaleupServiceBusEvent> scaleupServiceBusEventApplicationListener(LeaderElectionManager electionManager,
                                                                                                 ServiceInstanceScaler serviceInstanceScaler) {
        return new ScaleupServiceBusEventListener(electionManager, serviceInstanceScaler);
    }

    @Bean
    public ApplicationListener<ScaledownServiceBusEvent> scaledownServiceBusEventApplicationListener(LeaderElectionManager electionManager,
                                                                                                     ServiceInstanceShutdownInitiator shutdownInitiator) {
        return new ScaledownServiceBusEventListener(electionManager, shutdownInitiator);
    }

    @Bean
    public CooldownTimer cooldownTimer(ServiceStateManager serviceStateManager,
                                       ServiceClusterStateManager serviceClusterStateManager,
                                       ApplicationEventPublisher context,
                                       CloudManagerProperties config) {
        return new CooldownTimer(serviceStateManager, serviceClusterStateManager, context, config);
    }

    @Bean
    public KillServiceInstanceListener killServiceInstanceListener(CooldownTimer cooldownTimer,
                                                                   AwsSdkWrapper aws) {
        return new EcsKillServiceInstanceListener(cooldownTimer, aws);
    }

    @Bean
    public ServiceInstanceScaler serviceInstanceScaler(ServiceStateManager serviceStateManager,
                                                       CooldownTimer cooldownTimer,
                                                       AwsSdkWrapper aws) {
        return new EcsServiceInstanceScaler(serviceStateManager, cooldownTimer, aws);
    }

    @Bean
    public ServiceInstanceShutdownInitiator serviceInstanceShutdownInitiator(EurekaClient discoveryClient,
                                                                             ServiceStateManager serviceStateManager,
                                                                             ApplicationEventPublisher context) {
        return new ServiceInstanceShutdownInitiator(discoveryClient, serviceStateManager, context);
    }

    @Bean
    public Endpoint stateEndpoint(ServiceStateManager serviceStateManager, ServiceClusterStateManager serviceClusterStateManager) {
        return new StateEndpoint(serviceStateManager, serviceClusterStateManager);
    }

    @Bean
    public ServiceClusterScaler serviceClusterScaler(AwsSdkWrapper aws) {
        return new EcsServiceClusterScaler(aws);
    }

    @Bean
    public ServiceClusterMonitor serviceClusterMonitor(CloudManagerProperties config,
                                                       ServiceClusterStateManager stateManager,
                                                       CooldownTimer cooldownTimer,
                                                       ServiceClusterScaler scaler, LeaderElectionManager electionManager) {
        return new ServiceClusterMonitor(config, stateManager, cooldownTimer, scaler,electionManager);
    }

}
