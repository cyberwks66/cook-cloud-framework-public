package com.cooksys.cloud.router.core;

import com.github.zafarkhaja.semver.Version;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Created by timd on 3/2/17.
 */
public class EdgeProxyRouteManagerTest {

    EdgeProxyRouteManager routeManager;

    @Before
    public void init() {
        routeManager = new EdgeProxyRouteManager();

        DynamicRoute route1 = new DynamicRoute();
        route1.setServiceId("hello");
        route1.setvHost("hello.cooksys.com");

        Map<String,Version> proxyRoutes1 = new HashMap<>();
        proxyRoutes1.put("/hello/1/1/1",Version.valueOf("1.1.1"));
        proxyRoutes1.put("/hello/2/2/2",Version.valueOf("2.2.2"));
        route1.setProxyRoutes(proxyRoutes1);

        routeManager.putRoute(route1);

        DynamicRoute route2 = new DynamicRoute();
        route2.setServiceId("goodbye");
        route2.setvHost("goodbye.cooksys.com");

        Map<String,Version> proxyRoutes2 = new HashMap<>();
        proxyRoutes2.put("/goodbye/1/1/1",Version.valueOf("1.1.1"));
        proxyRoutes2.put("/goodbye/2/2/2",Version.valueOf("2.2.2"));
        route2.setProxyRoutes(proxyRoutes2);

        routeManager.putRoute(route2);
    }

    @Test
    public void testPutRoute() {
        assertTrue(routeManager.routes.get().containsKey("hello.cooksys.com"));
        assertTrue(routeManager.routes.get().containsKey("goodbye.cooksys.com"));
    }

    @Test
    public void testGetRoutes() {
        assertTrue(routeManager.getRoutes().size()==2);
    }

    @Test
    public void testGetMatchingRoute() {
        Optional<DynamicRoute> route = routeManager.getMatchingRoute("hello.cooksys.com");
        assertTrue(route.isPresent());

        assertTrue(route.get().getServiceId().equals("hello"));
    }
}