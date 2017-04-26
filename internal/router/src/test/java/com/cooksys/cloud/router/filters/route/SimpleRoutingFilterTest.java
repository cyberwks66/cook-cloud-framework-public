package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.StringTokenizer;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by timd on 3/16/17.
 */
public class SimpleRoutingFilterTest {
    private static final String HOST_HEADER = "host";
    private static final String HTTP = "http";
    public static final int HTTP_OK = 200;

    private RequestContextFactory contextFactory;
    private HttpClientFactory httpClientFactoryMock;

    private HttpServletRequest requestMock;

    private HttpClient httpClientMock;
    private HttpResponse responseMock;

    private SimpleRoutingFilter routingFilter;

    @Before
    public void setUp() {
        contextFactory = new RequestContextFactory();
        requestMock = mock(HttpServletRequest.class);
        httpClientFactoryMock = mock(HttpClientFactory.class);

        httpClientMock = mock(HttpClient.class);
        responseMock = mock(HttpResponse.class);

        doReturn(httpClientMock).when(httpClientFactoryMock).getHttpClient();
    }

    private void resetMocks() {
        reset(requestMock, httpClientFactoryMock, httpClientMock, responseMock);
        contextFactory.getZuulRequestContext().clear();
    }

    @Test
    public void testSimpleRoute() throws Exception {
        routingFilter = new SimpleRoutingFilter(contextFactory, httpClientFactoryMock);

        contextFactory.getZuulRequestContext().put(ContextConstants.SIMPLE_ROUTE_HOST, "originhost");
        contextFactory.getZuulRequestContext().put(ContextConstants.SIMPLE_ROUTE_PORT, 8888);
        contextFactory.getZuulRequestContext().put(ContextConstants.SIMPLE_ROUTE_PATH, "/some/path");
        contextFactory.getZuulRequestContext().setRequest(requestMock);
        HttpServletResponse servletResponseMock = mock(HttpServletResponse.class);
        contextFactory.getZuulRequestContext().setResponse(servletResponseMock);

        final String httpVerb = "GET";
        final String hostname = "zuulhost";

        doReturn(httpVerb).when(requestMock).getMethod();
        doReturn(HTTP).when(requestMock).getScheme();
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

        assertTrue(hostArgument.getValue().getHostName().equals("originhost"));
        assertTrue(hostArgument.getValue().getSchemeName().equalsIgnoreCase(HTTP));
        assertTrue(hostArgument.getValue().getPort() == 8888);
    }
}