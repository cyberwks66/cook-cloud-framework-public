package com.cooksys.cloud.router.actuator;

import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.router.core.TrafficRatioManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Class for custom actuator endpoints
 *
 * @author Tim Davidson
 */
@Configuration
public class ActuatorConfiguration {

    @Bean
    public RoutesController routeController(RoutesEndpoint delegate, RouteManager routeManager, TrafficRatioManager trafficRatioManager, ApplicationContext context) {
        return new RoutesController(delegate, routeManager, trafficRatioManager,context);
    }

    @Bean
    public RoutesEndpoint routesEndpoint() {
        return new RoutesEndpoint();
    }

}
