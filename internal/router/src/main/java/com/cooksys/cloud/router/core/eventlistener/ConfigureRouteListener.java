package com.cooksys.cloud.router.core.eventlistener;

import com.cooksys.cloud.router.core.DynamicRoute;
import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.commons.event.router.ConfigureRouteBusEvent;
import com.github.zafarkhaja.semver.Version;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Event listener for {@link ConfigureRouteBusEvent}
 *
 * @author Tim Davidson
 */
public class ConfigureRouteListener implements ApplicationListener<ConfigureRouteBusEvent>{

    private RouteManager routeManager;

    public ConfigureRouteListener(RouteManager routeManager) {
        this.routeManager = routeManager;
    }

    @Override
    public void onApplicationEvent(ConfigureRouteBusEvent event) {
        final DynamicRoute route = new DynamicRoute();
        route.setServiceId(event.getServiceId());
        route.setDefaultVersion(Version.valueOf(event.getDefaultVersion()));
        route.setvHost(event.getvHost());
        route.setProxyRoutes(new HashMap<>());

        //TODO java8 stream
        for(Map.Entry<String,String> proxyRoute : event.getProxyRoutes().entrySet()) {
            route.getProxyRoutes().put(proxyRoute.getKey(), Version.valueOf(proxyRoute.getValue()));
        }

        routeManager.putRoute(route);
    }
}
