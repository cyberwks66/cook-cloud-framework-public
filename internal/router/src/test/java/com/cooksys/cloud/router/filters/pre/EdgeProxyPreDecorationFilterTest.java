package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.DynamicRoute;
import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.github.zafarkhaja.semver.Version;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.monitoring.CounterFactory;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit test for EdgeProxyPreDecorationFilter
 */
public class EdgeProxyPreDecorationFilterTest {

    private HttpServletRequest requestMock;
    private RequestContext context;
    private RouteManager routeManager;
    private RequestContextFactory contextFactory;
    private RouterProperties config;
    private DynamicRoute route;
    private EdgeProxyPreDecorationFilter filter;

    @Before
    public void setUp() {
        contextFactory = new RequestContextFactory();
        context = contextFactory.getZuulRequestContext();
        requestMock = mock(HttpServletRequest.class);
        context.setRequest(requestMock);
        routeManager = mock(RouteManager.class);
        config = new RouterProperties();

        route = new DynamicRoute();
        route.setServiceId("hello");
        route.setvHost("hello.cooksys.com");

        Map<String, Version> proxyRoutes = new HashMap<>();
        proxyRoutes.put("/hello/1/1/1", Version.valueOf("1.1.1"));
        proxyRoutes.put("/hello/2/2/2", Version.valueOf("2.2.2"));
        route.setProxyRoutes(proxyRoutes);

        filter = new EdgeProxyPreDecorationFilter(routeManager, contextFactory, config);

        CounterFactory.initialize(new CounterFactory() {
            @Override
            public void increment(String name) {
            }
        });
    }

    private void resetMocks() {
        reset(requestMock,routeManager);
        context.clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRunWithVersion() throws Exception {
        doReturn(Optional.of(route)).when(routeManager).getMatchingRoute(any());
        doReturn("hello.cooksys.com").when(requestMock).getHeader("host");
        doReturn("/_/1/1/1/_/hello/world").when(requestMock).getServletPath();

        filter.run();

        assertTrue("hello".equals(context.get(ContextConstants.ROUTE_SERVICE_ID)));
        assertTrue(Version.valueOf("1.1.1").equals(context.get(ContextConstants.REQUEST_VERSION)));
        assertTrue(Version.valueOf("1.1.1").equals(context.get(ContextConstants.ROUTE_VERSION)));
        assertTrue("/hello/world".equals(context.get(ContextConstants.ROUTE_PATH)));

        Map<String, Version> proxyRoutes = (Map<String, Version>) context.get(ContextConstants.ROUTE_PROXY_ROUTES);

        assertTrue(proxyRoutes.get("/hello/1/1/1").equals(Version.valueOf("1.1.1")));
        assertTrue(proxyRoutes.get("/hello/2/2/2").equals(Version.valueOf("2.2.2")));

        resetMocks();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRunWithoutVersion() throws Exception {
        doReturn(Optional.of(route)).when(routeManager).getMatchingRoute(any());
        doReturn("hello.cooksys.com").when(requestMock).getHeader("host");
        doReturn("/hello/world").when(requestMock).getServletPath();
        doReturn("").when(requestMock).getQueryString();

        filter.run();

        assertTrue("hello".equals(context.get(ContextConstants.ROUTE_SERVICE_ID)));
        assertTrue(context.get(ContextConstants.REQUEST_VERSION)==null);
        assertTrue(context.get(ContextConstants.ROUTE_VERSION)==null);
        assertTrue("/hello/world".equals(context.get(ContextConstants.ROUTE_PATH)));
        Map<String,Version> proxyRoutes= (Map<String, Version>)context.get(ContextConstants.ROUTE_PROXY_ROUTES);
        assertTrue(proxyRoutes.get("/hello/1/1/1").equals(Version.valueOf("1.1.1")));
        assertTrue(proxyRoutes.get("/hello/2/2/2").equals(Version.valueOf("2.2.2")));

        resetMocks();
    }

    @Test
    public void missingRouteErrorHandling() throws Exception {
        doReturn("hello.cooksys.com").when(requestMock).getHeader("host");

        filter.run();

        assertTrue(context.getThrowable().getMessage().startsWith("Could not find a matching route"));
        assertTrue(context.get(ContextConstants.ERROR_EXCEPTION) instanceof RuntimeException);
        assertTrue(context.get(ContextConstants.ERROR_STATUS_CODE).equals(500));

        resetMocks();
    }
}























