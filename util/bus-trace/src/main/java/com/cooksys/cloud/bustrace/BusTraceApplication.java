package com.cooksys.cloud.bustrace;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Created by timd on 4/13/17.
 */
@SpringBootApplication
public class BusTraceApplication {

    @Bean
    public BusTraceProperties busTraceProperties() {
        return new BusTraceProperties();
    }

    @Bean
    public CommandLineRunner busTraceCommandLineRunner(BusTraceProperties config) {
        return new BusTraceCommandLineRunner(config);
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BusTraceApplication.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
    }
}
