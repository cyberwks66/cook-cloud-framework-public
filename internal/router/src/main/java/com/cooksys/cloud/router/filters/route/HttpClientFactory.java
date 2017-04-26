package com.cooksys.cloud.router.filters.route;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Apache HTTP client wrapper class
 *
 * @author Tim Davidson
 */
public class HttpClientFactory {

    private AtomicReference<HttpClient> client;

    @PostConstruct
    public void init() {
        client = new AtomicReference<>(newClient());
    }

    public HttpClient getHttpClient() {
        return client.get();
    }

    private HttpClient newClient() {
        return HttpClientBuilder.create().build();
    }
}
