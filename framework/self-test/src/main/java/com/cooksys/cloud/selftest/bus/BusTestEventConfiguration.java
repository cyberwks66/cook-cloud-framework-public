package com.cooksys.cloud.selftest.bus;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for BusTest API
 *
 * Note on custom bus events:
 * According to spring cloud documentation, to broadcast custom events, they must
 * fall under a subpackage of of org.springframework.cloud.bus.event
 * <p>
 * There is documentation here that provides a workaround for using custom packages:
 * https://github.com/spring-cloud/spring-cloud-bus/blob/master/docs/src/main/asciidoc/spring-cloud-bus.adoc
 * <p>
 * This configuration class implements that workaround using @RemoteApplicationEventScan
 */
@Configuration
@RemoteApplicationEventScan
public class BusTestEventConfiguration {

    @Value("${spring.application.name:self-test}")
    public String applicationName;

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    @Bean
    public DiscoveryClientService discoveryService(DiscoveryClient discoveryClient) {
        return new DiscoveryClientService(discoveryClient, applicationName);
    }

    @Bean
    public BusTestService busTestService(ApplicationEventPublisher context) {
        return new BusTestService(context, applicationName);
    }

    @Bean
    public BusTestAckRemoteEventListener busTestAckRemoteEventListener(BusTestService busTestManager) {
        return new BusTestAckRemoteEventListener(busTestManager);
    }

    @Bean
    public BusTestRemoteEventListener busTestRemoteEventListener(BusTestService busTestManager) {
        return new BusTestRemoteEventListener(busTestManager, instanceId);
    }
}
