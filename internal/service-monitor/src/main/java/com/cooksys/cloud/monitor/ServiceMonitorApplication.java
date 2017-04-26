package com.cooksys.cloud.monitor;

import com.cooksys.cloud.commons.event.EventConfiguration;
import com.cooksys.cloud.commons.leaderelection.EnableLeaderElection;
import com.cooksys.cloud.commons.util.EnableStaticContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Main entry point to the service-monitor application
 *
 * @author Tim Davidson
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.cooksys.cloud.monitor")
@EnableLeaderElection
@EnableStaticContext
@Import(EventConfiguration.class)
public class ServiceMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceMonitorApplication.class,args);
    }
}
