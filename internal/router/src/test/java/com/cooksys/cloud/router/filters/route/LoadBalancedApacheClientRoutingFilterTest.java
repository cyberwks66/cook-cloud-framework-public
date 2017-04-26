package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.filters.route.ribbon.VersionAwareRibbonClient;
import com.github.zafarkhaja.semver.Version;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.monitoring.CounterFactory;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.client.ServiceInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit test for LoadBalancedRouteingFilter
 */
public class LoadBalancedApacheClientRoutingFilterTest {
    private static final String VERSION_HEADER = "version";
    private static final String HOST_HEADER = "host";
    private static final String HTTP = "http";
    public static final int HTTP_OK = 200;
    public static final String PROXY_HOST = "proxyHost";

    private RequestContextFactory contextFactory;
    private VersionAwareRibbonClient ribbonClientMock;
    private HttpClientFactory httpClientFactoryMock;

    private HttpServletRequest requestMock;

    private HttpClient httpClientMock;
    private HttpResponse responseMock;

    private LoadBalancedApacheClientRoutingFilter routingFilter;

    @Before
    public void init() {
        contextFactory = new RequestContextFactory();
        requestMock = mock(HttpServletRequest.class);
        ribbonClientMock = mock(VersionAwareRibbonClient.class);
        httpClientFactoryMock = mock(HttpClientFactory.class);

        httpClientMock = mock(HttpClient.class);
        responseMock = mock(HttpResponse.class);

        doReturn(httpClientMock).when(httpClientFactoryMock).getHttpClient();

    }

    private void resetMocks() {
        reset(requestMock, ribbonClientMock, httpClientFactoryMock, httpClientMock, responseMock);
        contextFactory.getZuulRequestContext().clear();
    }

    @Test
    public void basicRoutingProxyMode() throws IOException {
        final String serviceId = "hello-world";
        final String hostname = "hostname";
        final int port = 8080;
        final String version = "1.0.1";
        final String httpVerb = "GET";

        RouterProperties routerProperties = new RouterProperties();
        routerProperties.setEdgeProxyMode(false); // Proxy mode

        routingFilter = new LoadBalancedApacheClientRoutingFilter(contextFactory, ribbonClientMock, httpClientFactoryMock, routerProperties);

        ServiceInstance serviceInstance = new ServiceInstance() {
            @Override
            public String getServiceId() {
                return serviceId;
            }

            @Override
            public String getHost() {
                return hostname;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public URI getUri() {
                return null;
            }

            @Override
            public Map<String, String> getMetadata() {
                Map<String, String> metadata = new HashMap<>();
                metadata.put(VERSION_HEADER, version);
                return metadata;
            }
        };

        doReturn(serviceInstance).when(ribbonClientMock).getNextServerFromLoadBalancer(any(), any(),any());

        contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_SERVICE_ID, serviceId);
        contextFactory.getZuulRequestContext().setRequest(requestMock);
        HttpServletResponse servletResponseMock = mock(HttpServletResponse.class);
        contextFactory.getZuulRequestContext().setResponse(servletResponseMock);

        doReturn(httpVerb).when(requestMock).getMethod();
        doReturn(new StringTokenizer(HOST_HEADER)).when(requestMock).getHeaderNames();
        doReturn(hostname).when(requestMock).getHeader(HOST_HEADER);


        doReturn(responseMock).when(httpClientMock).execute(any(HttpHost.class), any(HttpRequest.class));

        StatusLine statusLine = new StatusLine() {
            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public int getStatusCode() {
                return HTTP_OK;
            }

            @Override
            public String getReasonPhrase() {
                return null;
            }
        };

        doReturn(statusLine).when(responseMock).getStatusLine();
        HttpEntity entity = mock(HttpEntity.class);
        doReturn(entity).when(responseMock).getEntity();
        doReturn(new Header[0]).when(responseMock).getHeaders(any());
        doReturn(new Header[0]).when(responseMock).getAllHeaders();

        ArgumentCaptor<HttpHost> hostArgument = ArgumentCaptor.forClass(HttpHost.class);
        ArgumentCaptor<HttpRequest> requestArgument = ArgumentCaptor.forClass(HttpRequest.class);

        routingFilter.run();

        verify(httpClientMock).execute(hostArgument.capture(), requestArgument.capture());

        assertTrue(hostArgument.getValue().getHostName().equals(hostname));
        assertTrue(hostArgument.getValue().getSchemeName().equalsIgnoreCase(HTTP));
        assertTrue(hostArgument.getValue().getPort() == port);

        resetMocks();
    }

    @Test
    public void basicRoutingEdgeMode() throws IOException {
        final String serviceId = "hello-world";
        final String hostname = "hostname";
        final String proxyHostName = "proxy1.cooksys.com";

        final int port = 8080;
        final String version = "1.0.1";
        final String httpVerb = "GET";

        Map<String,Version> proxyRoutes = new HashMap<>();

        proxyRoutes.put("/hello-world/1/0/1", Version.valueOf("1.0.1"));
        proxyRoutes.put("/hello-world/2/0/2",Version.valueOf("2.0.2"));

        RouterProperties routerProperties = new RouterProperties();
        routerProperties.setEdgeProxyMode(true); // Edge mode

        routingFilter = new LoadBalancedApacheClientRoutingFilter(contextFactory, ribbonClientMock, httpClientFactoryMock, routerProperties);

        ServiceInstance serviceInstance = new ServiceInstance() {
            @Override
            public String getServiceId() {
                return serviceId;
            }

            @Override
            public String getHost() {
                return hostname;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public URI getUri() {
                return null;
            }

            @Override
            public Map<String, String> getMetadata() {
                Map<String, String> metadata = new HashMap<>();
                metadata.put(VERSION_HEADER, version);
                metadata.put(PROXY_HOST, proxyHostName);
                return metadata;
            }
        };

        doReturn(serviceInstance).when(ribbonClientMock).getNextServerFromLoadBalancer(any(), any(),any());

        contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_PROXY_ROUTES,proxyRoutes);
        contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_SERVICE_ID, serviceId);
        contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_PATH,"/hello");

        contextFactory.getZuulRequestContext().setRequest(requestMock);
        HttpServletResponse servletResponseMock = mock(HttpServletResponse.class);
        contextFactory.getZuulRequestContext().setResponse(servletResponseMock);

        doReturn(httpVerb).when(requestMock).getMethod();
        doReturn(new StringTokenizer(HOST_HEADER)).when(requestMock).getHeaderNames();
        doReturn(hostname).when(requestMock).getHeader(HOST_HEADER);

        doReturn(responseMock).when(httpClientMock).execute(any(HttpHost.class), any(HttpRequest.class));

        StatusLine statusLine = new StatusLine() {
            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public int getStatusCode() {
                return HTTP_OK;
            }

            @Override
            public String getReasonPhrase() {
                return null;
            }
        };

        doReturn(statusLine).when(responseMock).getStatusLine();
        HttpEntity entity = mock(HttpEntity.class);
        doReturn(entity).when(responseMock).getEntity();
        doReturn(new Header[0]).when(responseMock).getHeaders(any());
        doReturn(new Header[0]).when(responseMock).getAllHeaders();

        ArgumentCaptor<HttpHost> hostArgument = ArgumentCaptor.forClass(HttpHost.class);
        ArgumentCaptor<HttpRequest> requestArgument = ArgumentCaptor.forClass(HttpRequest.class);

        routingFilter.run();

        verify(httpClientMock).execute(hostArgument.capture(), requestArgument.capture());

        System.out.println("host: " + hostArgument.getValue().getHostName());
        assertTrue(hostArgument.getValue().getHostName().equals(proxyHostName));
        assertTrue(hostArgument.getValue().getSchemeName().equalsIgnoreCase(HTTP));
        assertTrue(hostArgument.getValue().getPort() == port);
        assertTrue("http://proxy1.cooksys.com:8080/hello-world/1/0/1/hello".equals(requestArgument.getValue().getRequestLine().getUri()));
        System.out.println("URI: " + requestArgument.getValue().getRequestLine().getUri());
        resetMocks();
    }

    @Test
    public void noRouteAvailableErrorHandling() {
        final String serviceId = "hello-world";

        RouterProperties routerProperties = new RouterProperties();
        routerProperties.setEdgeProxyMode(false); // Proxy mode

        routingFilter = new LoadBalancedApacheClientRoutingFilter(contextFactory, ribbonClientMock, httpClientFactoryMock, routerProperties);
        contextFactory.getZuulRequestContext().put(ContextConstants.ROUTE_SERVICE_ID, serviceId);
        doReturn(null).when(ribbonClientMock).getNextServerFromLoadBalancer(any(), any(),any());

        CounterFactory.initialize(new CounterFactory() {
            @Override
            public void increment(String name) {

            }
        });

        routingFilter.run();

        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.ERROR_STATUS_CODE).equals(500));
        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.ERROR_EXCEPTION) instanceof RuntimeException);
        assertTrue(contextFactory.getZuulRequestContext().getThrowable() instanceof ZuulException);

        RequestContext context = contextFactory.getZuulRequestContext();
        assertTrue(contextFactory.getZuulRequestContext().getThrowable().getMessage().startsWith("No route available"));

        resetMocks();
    }

    @Test
    public void missingProxyHostMetadataEdgeModeErrorHandling() {
        final String serviceId = "hello-world";
        final String hostname = "hostname";

        final int port = 8080;
        final String version = "1.0.1";

        RouterProperties routerProperties = new RouterProperties();
        routerProperties.setEdgeProxyMode(true); // Edge mode

        routingFilter = new LoadBalancedApacheClientRoutingFilter(contextFactory, ribbonClientMock, httpClientFactoryMock, routerProperties);

        ServiceInstance serviceInstance = new ServiceInstance() {
            @Override
            public String getServiceId() {
                return serviceId;
            }

            @Override
            public String getHost() {
                return hostname;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public URI getUri() {
                return null;
            }

            @Override
            public Map<String, String> getMetadata() {
                Map<String, String> metadata = new HashMap<>();
                metadata.put(VERSION_HEADER, version);
                return metadata;
            }
        };

        doReturn(serviceInstance).when(ribbonClientMock).getNextServerFromLoadBalancer(any(), any(),any());

        CounterFactory.initialize(new CounterFactory() {
            @Override
            public void increment(String name) {

            }
        });

        routingFilter.run();

        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.ERROR_STATUS_CODE).equals(500));
        assertTrue(contextFactory.getZuulRequestContext().get(ContextConstants.ERROR_EXCEPTION) instanceof RuntimeException);
        assertTrue(contextFactory.getZuulRequestContext().getThrowable() instanceof ZuulException);
        assertTrue(contextFactory.getZuulRequestContext().getThrowable().getMessage().startsWith("Unable to route to service"));
        resetMocks();
    }
}