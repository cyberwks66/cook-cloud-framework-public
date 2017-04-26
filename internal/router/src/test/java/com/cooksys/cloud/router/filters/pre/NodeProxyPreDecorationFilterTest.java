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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * Unit test for NodeProxyPreDecorationFilter
 */
public class NodeProxyPreDecorationFilterTest {

    private HttpServletRequest requestMock;
    private RequestContext context;
    private RouteManager routeManager;
    private RequestContextFactory contextFactory;
    private RouterProperties config;
    private DynamicRoute route;
    private NodeProxyPreDecorationFilter filter;

    private void resetMocks() {
        reset(requestMock,routeManager);
        context.clear();
    }

    @Before
    public void setUp() throws Exception {
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

        filter = new NodeProxyPreDecorationFilter(routeManager, contextFactory, config);

        CounterFactory.initialize(new CounterFactory() {
            @Override
            public void increment(String name) {
            }
        });
    }

    @Test
    public void testRun() throws Exception {
        doReturn(Optional.of(route)).when(routeManager).getMatchingRoute(any());
        doReturn("hello.cooksys.com").when(requestMock).getHeader("host");
        doReturn("/hello/1/1/1/hello/world").when(requestMock).getServletPath();

        filter.run();

        assertTrue("hello".equals(context.get(ContextConstants.ROUTE_SERVICE_ID)));
        assertTrue(Version.valueOf("1.1.1").equals(context.get(ContextConstants.ROUTE_VERSION)));
        assertTrue("/hello/world".equals(context.get(ContextConstants.ROUTE_PATH)));

        resetMocks();
    }
}