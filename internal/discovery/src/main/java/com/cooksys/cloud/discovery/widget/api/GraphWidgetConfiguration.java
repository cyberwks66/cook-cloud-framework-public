package com.cooksys.cloud.discovery.widget.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean definitions for graph widget api
 *
 * @author Tim Davidson
 */
@Configuration
public class GraphWidgetConfiguration {

    @Bean
    public GraphWidgetService graphWidgetService() {
        return new GraphWidgetServiceImpl();
    }
}
