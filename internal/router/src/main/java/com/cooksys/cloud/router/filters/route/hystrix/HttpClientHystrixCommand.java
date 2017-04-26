package com.cooksys.cloud.router.filters.route.hystrix;

import com.cooksys.cloud.router.util.ContextHelper;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Routes a request using HttpCient and wraps the command in a hystrix circuit breaker
 * (One circuit breaker per serviceId/version combo)
 *
 * @author Tim Davidson
 */
public class HttpClientHystrixCommand extends HystrixCommand<HttpResponse> {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientHystrixCommand.class);

    private final HttpClient httpClient;
    private final HttpHost httpHost;
    private final HttpRequest httpRequest;
    private final RequestContext context;

    public HttpClientHystrixCommand(HystrixMetadata metadata, HttpClient httpClient, HttpHost httpHost, HttpRequest httpRequest, RequestContext context) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(metadata.getThreadpoolGroup()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(metadata.getServiceId() + " " + metadata.getVersion())));
        this.httpClient=httpClient;
        this.httpHost=httpHost;
        this.httpRequest=httpRequest;
        this.context=context;
    }

    @Override
    protected HttpResponse run() {
        HttpResponse response = null;
        try {
            logger.info("RequestLine: " + httpRequest.getRequestLine());
            response = httpClient.execute(httpHost, httpRequest);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            ContextHelper.errorResponse(context,500,e);
        }
        return response;
    }

}