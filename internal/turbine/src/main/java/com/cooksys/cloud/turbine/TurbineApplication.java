package com.cooksys.cloud.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Main entrypoint for turbine application.  Starts an embedded Turbine server.
 *
 * @author Tim Davidson
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTurbine
@EnableHystrixDashboard
public class TurbineApplication {

    public static void main(String[] args) {
        final List<String> newArgs = new ArrayList<>();

        boolean aws = false;

        for (String arg : args) {
            if (arg.equals("-aws")) {
                aws = true;
                break;
            }
            newArgs.add(arg);
        }

        if (aws) {
            newArgs.add("--eureka.instance.hostName=" + getEc2HostIpAddress());
        }

        SpringApplication.run(TurbineApplication.class, newArgs.toArray(new String[newArgs.size()]));
    }

    public static String getEc2HostIpAddress() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://169.254.169.254/latest/meta-data/local-ipv4", String.class);
    }
}
