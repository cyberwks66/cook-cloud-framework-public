package com.cooksys.cloud.greetingservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingEndpoint {

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String getGreeting() {
        try {
            Thread.sleep(300l);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "Good Morning";
    }
}
