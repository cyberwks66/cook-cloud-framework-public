package com.cooksys.cloud.internal.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Main entry point for configuration service.  Launches an embedded Spring Cloud ConfigServer.
 *
 * @author Tim Davidson
 */
@EnableConfigServer
@SpringBootApplication
@EnableEurekaClient
public class EmbeddedConfigurationServer {
    public static void main(String[] args) {

        List<String> newArgs = new ArrayList<>();

        boolean aws = false;

        for(String arg : args) {
            if(arg.equals("-aws")) {
                aws=true;
                break;
            } else {
                newArgs.add(arg);
            }
        }

        if(aws) {
            newArgs.add("--eureka.instance.hostName=" + getEc2HostIpAddress());
        }

        SpringApplication.run(EmbeddedConfigurationServer.class, newArgs.toArray(new String[newArgs.size()]));
    }

    public static String getEc2HostIpAddress() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://169.254.169.254/latest/meta-data/local-ipv4",String.class);
    }
}
