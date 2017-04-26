package com.cooksys.cloud.router.core;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Route manager implementation for Edge Proxy mode.  Indexes the routes using vHost.
 *
 * @author Tim Davidson
 */
public class EdgeProxyRouteManager implements RouteManager {

    final AtomicReference<Map<String, DynamicRoute>> routes = new AtomicReference<>();

    public EdgeProxyRouteManager() {
        routes.set(new HashMap<>());
    }

    @Override
    public void putRoute(DynamicRoute route) {
        routes.get().put(route.getvHost(),route);
    }

    @Override
    public void deleteRoute(DynamicRoute route) {
        // placeholder
    }

    @Override
    public List<DynamicRoute> getRoutes() {
        return new ArrayList<>(routes.get().values());
    }

    @Override
    public Optional<DynamicRoute> getMatchingRoute(String vHost) {
        if(routes.get().containsKey(vHost)) {
            return Optional.of(routes.get().get(vHost));
        }
        return Optional.empty();
    }
}
