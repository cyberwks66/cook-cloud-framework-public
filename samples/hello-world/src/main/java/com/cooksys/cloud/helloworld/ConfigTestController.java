package com.cooksys.cloud.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigTestController {
    @Autowired
    private Properties props;

    @RequestMapping("/config")
    public String getGreeting() {
        return props.getGreeting();
    }
}
