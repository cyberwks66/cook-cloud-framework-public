package com.cooksys.cloud.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main entry point for the router application.  Launches an embedded Zuul server.
 *
 * @author Tim Davidson
 */
@SpringBootApplication
@EnableZuulServer
@EnableHystrixDashboard
@EnableDiscoveryClient
@EnableCircuitBreaker
@ComponentScan("com.cooksys.cloud.router")
public class RouterApplication {
    private static final String EUREKA_INSTANCE_INSTANCE_ID = "--eureka.instance.instance-id";
    private static final String SPRING_APPLICATION_INDEX = "--spring.application.index";

    public static void main(String[] args) {

        if (args.length == 1) {
            if (args[0].equals("-c") || args[0].equalsIgnoreCase("--configprops")) {
                printConfigHelp();
                System.exit(0);
            }
            if (args[0].equals("-help") || args[0].equals("--help")) {
                printUsage();
                System.exit(0);
            }
        }

        // Generate unique instanceId
        final String instanceId = UUID.randomUUID().toString();
        final List<String> newArgs = new ArrayList<>();

        boolean aws = false;

        for (String arg : args) {
            if (arg.equals("-aws")) {
                aws = true;
                break;
            } else if (!arg.startsWith(EUREKA_INSTANCE_INSTANCE_ID)) {
                newArgs.add(arg);
            }
        }

        if (aws) {
            newArgs.add("--eureka.instance.hostName=" + getEc2HostIpAddress());
        }
        newArgs.add(EUREKA_INSTANCE_INSTANCE_ID + "=" + instanceId);

        // For spring-cloud-bus, application contextId must be unique as well, or events will not get funneled to their listeners.
        // Setting the following property will ensure that the contextId is unique
        newArgs.add(SPRING_APPLICATION_INDEX + "=" + instanceId);

        SpringApplication.run(RouterApplication.class, newArgs.toArray(new String[newArgs.size()]));
    }

    private static void printUsage() {
        System.out.println("\n" +
                "usage: docker run router [-c | --configprops]\n" +
                "\n" +
                "Arguments:\n" +
                "\n" +
                "  -c, --configprops    Prints the required configuration properties in YAML format\n" +
                "\n");
    }

    private static void printConfigHelp() {
        final String defaultConfig = "server:\n" +
                "  port: 8764\n" +
                "\n" +
                "router:\n" +
                "  # Set this to false when running in Node-Proxy mode\n" +
                "  edgeProxyMode: true\n" +
                "  \n" +
                "spring:\n" +
                "  application:\n" +
                "    name: router-edge\n" +
                "  rabbitmq:\n" +
                "    # Change host to actual RabbitMQ host\n" +
                "    host: localhost\n" +
                "    # Default RabbitMQ port is 5672\n" +
                "    port: 5672\n" +
                "    # Default username and password for RabbitMQ is guest/guest\n" +
                "    username: guest\n" +
                "    password: guest\n" +
                "\n" +
                "eureka:\n" +
                "  client:\n" +
                "    serviceUrl:\n" +
                "      # Change host to match eureka host\n" +
                "      defaultZone: http://localhost:8761/eureka\n";

        System.out.println("Minimum required properties to run Router application: \n\n" + defaultConfig);
    }

    public static String getEc2HostIpAddress() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://169.254.169.254/latest/meta-data/local-ipv4", String.class);
    }
}
