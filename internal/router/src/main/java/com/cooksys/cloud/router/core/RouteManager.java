package com.cooksys.cloud.router.core;

import java.util.List;
import java.util.Optional;

/**
 * Abstract route manager - implementations are specific to router' mode.  Also a handler for ConfigureRouteBusEvent.
 *
 * @author Tim Davidson
 */
public interface RouteManager {
    public void putRoute(DynamicRoute route);

    public void deleteRoute(DynamicRoute route);

    public List<DynamicRoute> getRoutes();

    public Optional<DynamicRoute> getMatchingRoute(String key);

}
