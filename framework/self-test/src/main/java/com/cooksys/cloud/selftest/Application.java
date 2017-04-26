package com.cooksys.cloud.selftest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main entry point to self-test aplication
 *
 * @author timd
 */
@SpringBootApplication
@EnableEurekaClient
public class Application {

    private static final String EUREKA_INSTANCE_INSTANCE_ID = "--eureka.instance.instance-id";
    private static final String SPRING_APPLICATION_INDEX = "--spring.application.index";

    public static void main(String[] args) {

        //TODO - this eventually will be common code for all framework components - this should be wrapped in the SDK or some common place
        // Generate unique instanceId
        final String instanceId = UUID.randomUUID().toString();
        final List<String> newArgs = new ArrayList<>();

        for(String arg : args) {
            if(!arg.startsWith(EUREKA_INSTANCE_INSTANCE_ID)) {
                newArgs.add(arg);
            }
        }

        newArgs.add(EUREKA_INSTANCE_INSTANCE_ID + "=" + instanceId);

        // For spring-cloud-bus, application contextId must be unique as well, or events will not get funneled to their listeners.
        // Setting the following property will ensure that the contextId is unique
        newArgs.add(SPRING_APPLICATION_INDEX + "=" + instanceId);


        SpringApplication.run(Application.class,newArgs.toArray(new String[0]));
    }
}
