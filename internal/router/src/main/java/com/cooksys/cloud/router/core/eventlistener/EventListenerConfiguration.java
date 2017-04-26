package com.cooksys.cloud.router.core.eventlistener;

import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.router.core.TrafficRatioManager;
import com.cooksys.cloud.commons.event.router.ConfigureRouteBusEvent;
import com.cooksys.cloud.commons.event.router.ConfigureTrafficRatioBusEvent;
import com.cooksys.cloud.commons.event.router.RemoveRouteBusEvent;
import com.cooksys.cloud.commons.event.router.RemoveTrafficRatioBusEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring bean definitions for bus event listeners
 *
 * @author Tim Davidson
 */
@Configuration
public class EventListenerConfiguration {

    @Bean
    public ApplicationListener<ConfigureRouteBusEvent> configureRouteListener(RouteManager routeManager) {
        return new ConfigureRouteListener(routeManager);
    }

    @Bean
    public ApplicationListener<RemoveRouteBusEvent> removeRouteListener(RouteManager routeManager) {
        return new RemoveRouteListener(routeManager);
    }

    @Bean
    public ApplicationListener<ConfigureTrafficRatioBusEvent> configureTrafficRatioListener(TrafficRatioManager trafficRatioManager) {
        return new ConfigureTrafficRatioListener(trafficRatioManager);
    }

    @Bean public ApplicationListener<RemoveTrafficRatioBusEvent> removeTrafficRatioListener(TrafficRatioManager trafficRatioManager) {
        return new RemoveTrafficRatioListener(trafficRatioManager);
    }
}
