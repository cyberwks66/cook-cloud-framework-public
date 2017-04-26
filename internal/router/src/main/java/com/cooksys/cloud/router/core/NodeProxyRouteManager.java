package com.cooksys.cloud.router.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Route manager implementation for Node Proxy mode.  Indexes the routes using proxy path.
 *
 * @author Tim Davidson
 */
public class NodeProxyRouteManager implements RouteManager {

    final Map<String, DynamicRoute> routes = new ConcurrentHashMap<>();

    public NodeProxyRouteManager() {
    }

    /**
     * adds a zuul route
     * @param route route to add
     */
    @Override
    public void putRoute(DynamicRoute route) {
        // Index the routes using the proxy path as the key
        for(String proxyRoutePath : route.getProxyRoutes().keySet()) {
            routes.put(proxyRoutePath,route);
        }
    }

    /**
     * deletes a route
     * @param route route to add
     */
    @Override
    public void deleteRoute(DynamicRoute route) {
        // not sure this is necessary or how this would work with event bus, placeholder for now
    }

    /**
     * Returns all the current configured routes
     * @return list of all configured routes
     */
    @Override
    public List<DynamicRoute> getRoutes() {
        // Since we indexed using proxy key, there may be some duplicates - remove them here
        final List<DynamicRoute> routeList = new ArrayList<>();

        for(DynamicRoute route : routes.values()) {
            if(!routeList.contains(route)) {
                routeList.add(route);
            }
        }

        return new ArrayList<>(routeList);
    }

    /**
     * Returns the route with matching path prefix
     * @param path the servletPath of the request
     * @return Optional of matching route
     */
    @Override
    public Optional<DynamicRoute> getMatchingRoute(String path) {

        for(Map.Entry<String,DynamicRoute> routeEntry : routes.entrySet()) {
            if(path.startsWith(routeEntry.getKey())) {
                return Optional.of(routeEntry.getValue());
            }
        }
        return Optional.empty();
    }
}
