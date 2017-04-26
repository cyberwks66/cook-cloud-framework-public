package com.cooksys.cloud.sdk.core;

import com.google.gson.Gson;
import com.netflix.appinfo.HealthCheckHandler;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Bean Configuration for Cloud SDK
 *
 * @author Tim Davidson
 */
@Configuration
public class CloudSdkConfiguration {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public HealthIndicator decommissionHealthIndicator(EurekaInstanceConfigBean eurekaInstanceProps) {
        return new DecommissionServiceInstanceBusEventListener(eurekaInstanceProps);
    }

    @Bean
    public HealthAggregator healthAggregator() {
        return new OrderedHealthAggregator();
    }

    @Bean
    public HealthCheckHandler healthCheckHandler(HealthAggregator healthAggregator) {
        return new ServiceHealthAwareHealthCheckHandler(healthAggregator);
    }
}
