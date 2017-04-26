package com.cooksys.cloud.helloworld.rest.controllers;

import com.cooksys.cloud.helloworld.model.HelloResponse;
import com.cooksys.cloud.sdk.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

@RestController
public class HelloWorldController {

    private static Logger logger = Logger.getLogger(String.valueOf(HelloWorldController.class));

    @Autowired
    private GreetingCircuitBreaker greetingCircuitBreaker;

    @Value("${eureka.instance.metadataMap.version:default}")
    private String versionMetadata;

    @Value("${hello-world.greeting}")
    private String greeting;

    @Value("${hello-world.name}")
    private String name;

    @RequestMapping("/hello")
    public HelloResponse sayHello() {
        HelloResponse response = new HelloResponse();
        response.setVersion(versionMetadata);

        response.setGreeting(greetingCircuitBreaker.getGreeting());
        InetAddress ip = null;

        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //response.setName(ip.getHostAddress());
        response.setName(name);
        response.setGreeting(greeting);

        return response;
    }

    @RequestMapping("/beantest")
    public String beanTest() {

        ApplicationContext context = SpringContext.getApplicationContext();
        RestTemplate restTemplate = (RestTemplate) context.getBean("loadBalancedRestTemplate");

        return ("restTemplate == null " + (restTemplate == null));

    }

    public GreetingCircuitBreaker getGreetingCircuitBreaker() {
        return greetingCircuitBreaker;
    }

    public void setGreetingCircuitBreaker(GreetingCircuitBreaker greetingCircuitBreaker) {
        this.greetingCircuitBreaker = greetingCircuitBreaker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
