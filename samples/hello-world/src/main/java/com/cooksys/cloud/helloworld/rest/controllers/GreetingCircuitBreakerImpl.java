package com.cooksys.cloud.helloworld.rest.controllers;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GreetingCircuitBreakerImpl implements GreetingCircuitBreaker {

    @Autowired
    private RestTemplate restTemplate;


    @HystrixCommand(fallbackMethod = "getGreetingCallback")
    @Override
    public String getGreeting() {
        return restTemplate.getForObject("http://greeting-service/greeting", String.class);
    }

    public String getGreetingCallback() {
        return "Hello from Circuit Breaker";
    }


}
