package com.cooksys.cloud.router.core;

import com.cooksys.cloud.router.configuration.RouteConfigurationLoader;
import com.cooksys.cloud.router.configuration.RouterProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Core Router configuration class
 *
 * @author Tim Davidson
 */
@Configuration
public class CoreConfiguration {

    @Bean
    public RouteManager routeManager(RouterProperties config) {
        if(config.isEdgeProxyMode()) {
            return new EdgeProxyRouteManager();
        } else {
            return new NodeProxyRouteManager();
        }
    }

    @Bean
    public TrafficRatioManager trafficRatioManager() {
        return new TrafficRatioManagerImpl();
    }

    @Bean(initMethod = "init")
    @RefreshScope
    public RouteConfigurationLoader routeConfigurationLoader(RouterProperties routerProperties,
                                                             RouteManager routeManager) {
        return new RouteConfigurationLoader(routerProperties,routeManager);
    }

    @Bean
    public RequestContextFactory zuulRequestContextWrapper() {
        return new RequestContextFactory();
    }

}
