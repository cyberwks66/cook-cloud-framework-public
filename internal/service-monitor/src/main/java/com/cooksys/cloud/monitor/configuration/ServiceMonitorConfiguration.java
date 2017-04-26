package com.cooksys.cloud.monitor.configuration;

import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import com.cooksys.cloud.monitor.core.DiscoveryHeartbeatInstanceCountUpdator;
import com.cooksys.cloud.monitor.core.ServiceMetricEventListener;
import com.cooksys.cloud.monitor.core.ServiceThroughputTracker;
import com.cooksys.cloud.monitor.core.TurbineStreamConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean definitions for service-monitor application
 *
 * @author Tim Davidson
 */
@Configuration
public class ServiceMonitorConfiguration {
    @Bean(destroyMethod = "closeTurbineStream")
    public TurbineStreamConnectionManager turbineConnectionFactory(DiscoveryClient discoveryClient,
                                                                   LeaderElectionManager electionManager,
                                                                   ApplicationContext context) {
        return new TurbineStreamConnectionManager(discoveryClient, electionManager, context);
    }

    @Bean
    @ConfigurationProperties(prefix = "serviceMonitor")
    public ServiceMonitorProperties serviceMonitorProperties() {
        return new ServiceMonitorProperties();
    }

    @Bean
    public ServiceThroughputTracker serviceThroughputTracker() {
        return new ServiceThroughputTracker();

    }
    @Bean
    public ServiceMetricEventListener serviceMetricEventListener(ApplicationContext context,
                                                                 ServiceMonitorProperties config,
                                                                 ServiceThroughputTracker serviceThroughputTracker) {
        return new ServiceMetricEventListener(context,config,serviceThroughputTracker);
    }

    @Bean
    public ApplicationListener<HeartbeatEvent> discoveryHeartbeatInstanceCountUpdator(ServiceThroughputTracker serviceThroughputTracker,
                                                                                      com.netflix.discovery.DiscoveryClient discoveryClient) {
        return new DiscoveryHeartbeatInstanceCountUpdator(serviceThroughputTracker,discoveryClient);
    }

}
