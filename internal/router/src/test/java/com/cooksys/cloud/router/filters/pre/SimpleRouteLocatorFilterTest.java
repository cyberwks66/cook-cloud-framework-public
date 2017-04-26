package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by timd on 3/16/17.
 */
public class SimpleRouteLocatorFilterTest {

    private RequestContextFactory contextFactory = new RequestContextFactory();
    private RouterProperties config = new RouterProperties();

    @Before
    public void setUp() {
        config.setSimpleProxy(new RouterProperties.SimpleProxy());
        config.getSimpleProxy().setEnabled(true);
        config.getSimpleProxy().setRoutes(new HashMap<>());
        config.getSimpleProxy().getRoutes().put("test",new RouterProperties.SimpleProxy.SimpleRoute());
        config.getSimpleProxy().getRoutes().get("test").setProxyHost("proxyhost");
        config.getSimpleProxy().getRoutes().get("test").setProxyPort(8888);

        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("/context/some/path").when(requestMock).getServletPath();
        contextFactory.getZuulRequestContext().setRequest(requestMock);
    }
    @Test
    public void simpleRouteLocatorTest() throws Exception {


        config.getSimpleProxy().getRoutes().get("test").setContextPath("/context");

        SimpleRouteLocatorFilter filter = new SimpleRouteLocatorFilter(contextFactory,config);

        filter.run();

        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.SIMPLE_ROUTE_PATH).equals("/some/path"));
        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.SIMPLE_ROUTE_HOST).equals("proxyhost"));
        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.SIMPLE_ROUTE_PORT).equals(8888));
    }

    @Test
    public void simpleRouteLocatorTestTrailingSlash() throws Exception {


        config.getSimpleProxy().getRoutes().get("test").setContextPath("/context/");

        SimpleRouteLocatorFilter filter = new SimpleRouteLocatorFilter(contextFactory,config);

        filter.run();

        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.SIMPLE_ROUTE_PATH).equals("/some/path"));
        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.SIMPLE_ROUTE_HOST).equals("proxyhost"));
        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.SIMPLE_ROUTE_PORT).equals(8888));
    }

}