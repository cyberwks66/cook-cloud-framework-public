package com.cooksys.cloud.cloudmanager;

import com.cooksys.cloud.commons.event.EventConfiguration;
import com.cooksys.cloud.commons.leaderelection.EnableLeaderElection;
import com.cooksys.cloud.commons.util.EnableStaticContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main entry point to cloud-manager application
 *
 * @author Tim Davidson
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan("com.cooksys.cloud.cloudmanager")
@EnableLeaderElection
@EnableStaticContext
@Import(EventConfiguration.class)
public class CloudManagerApplication {
    private static final String SPRING_APPLICATION_INDEX = "--spring.application.index";

    public static void main(String[] args) {

        // Generate unique instanceId
        final String instanceId = UUID.randomUUID().toString();
        final List<String> newArgs = new ArrayList<>();

        // For spring-cloud-bus, application contextId must be unique as well, or events will not get funneled to their listeners.
        // Setting the following property will ensure that the contextId is unique
        newArgs.add(SPRING_APPLICATION_INDEX + "=" + instanceId);
        newArgs.add("--eureka.instance.instance-id="+instanceId);

        for(String arg : args) {
            newArgs.add(arg);
        }


        SpringApplication.run(CloudManagerApplication.class,newArgs.toArray(new String[newArgs.size()]));
    }
}
