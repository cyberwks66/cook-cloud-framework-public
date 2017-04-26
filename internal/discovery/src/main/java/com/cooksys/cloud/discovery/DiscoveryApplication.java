package com.cooksys.cloud.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for Discovery service - starts an embedded Eureka server
 *
 * @author Tim Davidson
 */
@SpringBootApplication
@EnableEurekaServer
@ComponentScan("com.cooksys.cloud")
public class DiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }

}
