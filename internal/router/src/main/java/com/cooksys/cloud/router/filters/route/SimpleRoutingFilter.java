package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.util.ContextHelper;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Routing filter that simply forwards a request to the host and port specified in the request context.
 * <p>
 * This filter is used when routing to non-netflix enabled origins
 *
 * @author Tim Davidson
 */
public class SimpleRoutingFilter extends ApacheClientRoutingFilter {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRoutingFilter.class);

    public SimpleRoutingFilter(RequestContextFactory contextFactory, HttpClientFactory httpClientFactory) {
        super(contextFactory, httpClientFactory);
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.SIMPLE_ROUTING_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.SIMPLE_ROUTING_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() {
        logger.info("executing SimpleRoutingFilter");
        try {
            final RequestContext context = contextFactory.getZuulRequestContext();

            final HttpServletRequest request = context.getRequest();
            final Header[] headers = buildZuulRequestHeaders(request);
            final String verb = getVerb(request);

            InputStream requestEntity;
            try {
                requestEntity = request.getInputStream();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            final HttpClient httpclient = httpClientFactory.getHttpClient();

            final String routeHost = (String) context.get(ContextConstants.SIMPLE_ROUTE_HOST);
            final String routePath = (String) context.get(ContextConstants.SIMPLE_ROUTE_PATH);
            final Integer routePort = (Integer) context.get(ContextConstants.SIMPLE_ROUTE_PORT);
            final String scheme = context.getRequest().getScheme();

            final URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(routeHost)
                    .setPort(routePort)
                    .setPath(routePath)
                    .setCustomQuery(request.getQueryString());

            final String uri = uriBuilder.toString();

            final HttpHost host = new HttpHost(routeHost, routePort);
            HttpRequest httpRequest;

            // only set the requestEntity if the request is POST or PUT
            switch (verb) {
                case POST:
                    httpRequest = new HttpPost(uri);
                    InputStreamEntity postEntity = new InputStreamEntity(requestEntity);
                    ((HttpPost) httpRequest).setEntity(postEntity);
                    break;
                case PUT:
                    httpRequest = new HttpPut(uri);
                    InputStreamEntity putEntity = new InputStreamEntity(requestEntity, request.getContentLength());
                    ((HttpPut) httpRequest).setEntity(putEntity);
                    break;
                default:
                    httpRequest = new BasicHttpRequest(verb, uri);
            }

            httpRequest.setHeaders(headers);

            HttpResponse response = null;
            HttpClient httpClient = httpClientFactory.getHttpClient();

            try {
                logger.info("RequestLine: " + httpRequest.getRequestLine());
                response = httpClient.execute(host, httpRequest);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                ContextHelper.errorResponse(context, 500, e);
            }

            setResponse(response);
        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(), 500, e);
        }
        return null;
    }
}
