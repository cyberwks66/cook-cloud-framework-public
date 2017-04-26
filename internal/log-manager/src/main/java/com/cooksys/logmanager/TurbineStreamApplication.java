package com.cooksys.logmanager;

import com.cooksys.cloud.commons.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class TurbineStreamApplication {

	private static final Logger log = LoggerFactory.getLogger(TurbineStreamApplication.class);

    @Bean
    public SpringContext springContext() {
        return new SpringContext();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TurbineStreamApplication.class,args);
    }


}


