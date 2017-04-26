package com.cooksys.cloud.router.configuration;

import com.cooksys.cloud.router.core.DynamicRoute;
import com.cooksys.cloud.router.core.RouteManager;
import com.github.zafarkhaja.semver.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Loader for routes in configuration service
 *
 * @author Tim Davidson
 */
public class RouteConfigurationLoader {
    public static final Logger logger = LoggerFactory.getLogger(RouteConfigurationLoader.class);

    private final RouterProperties routerProperties;
    private final RouteManager routeManager;

    public RouteConfigurationLoader(RouterProperties routerProperties, RouteManager routeManager) {
        this.routerProperties = routerProperties;
        this.routeManager=routeManager;
    }

    /**
     * Updates the Dynamic Route manager with newly configured routes when refresh endpoint is hit.  Will not delete or
     * replace any dynamic routes that are already configured, only adds
     */
    public void init() {
        if(routerProperties.getRoutes()==null) {
            return;
        }

        for(Map.Entry<String,RouterProperties.Route> route : routerProperties.getRoutes().entrySet()) {
            if(!routeExistsForServiceId(route.getKey())) {
                final Map<String, Version> proxyRoutes = new HashMap<>();

                for (Map.Entry<String, String> proxyRoute : route.getValue().getProxyRoutes().entrySet()) {
                    proxyRoutes.put(proxyRoute.getKey(), Version.valueOf(proxyRoute.getValue()));
                }

                DynamicRoute dynamicRoute = new DynamicRoute()
                        .setServiceId(route.getKey())
                        .setDefaultVersion(Version.valueOf(route.getValue().getDefaultVersion()))
                        .setvHost(route.getValue().getvHost())
                        .setProxyRoutes(proxyRoutes);

                routeManager.putRoute(dynamicRoute);
                //TODO update this to include traffic ratio

            }
        }
    }

    private boolean routeExistsForServiceId(String serviceId) {
        for(DynamicRoute route : routeManager.getRoutes()) {
            if(serviceId.equals(route.getServiceId())) {
                return true;
            }
        }
        return false;
    }
}
