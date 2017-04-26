package com.cooksys.cloud.router.filters.route.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

/**
 * Wraps a {@link org.springframework.web.client.RestTemplate} command in a Hystrix circuit breaker
 *
 * @author Tim Davidson
 */
public class RestTemplateHystrixCommand extends HystrixCommand<ResponseEntity<Resource>> {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateHystrixCommand.class);

    private final RestOperations restTemplate;
    private final RequestEntity<Resource> requestEntity;
    private final RequestContext context;
    private RestClientException restTemplateException;

    public RestTemplateHystrixCommand(HystrixMetadata metadata, RestOperations restTemplate, RequestEntity<Resource> requestEntity, RequestContext context) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(metadata.getThreadpoolGroup()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(metadata.getServiceId() +
                        ((metadata.getVersion()!=null && !metadata.getVersion().isEmpty()) ? " v" + metadata.getVersion() : ""))));
        this.restTemplate = restTemplate;
        this.requestEntity = requestEntity;
        this.context = context;
    }

    @Override
    protected ResponseEntity<Resource> run() throws Exception {
        ResponseEntity<Resource> response = null;
        try {
            response = restTemplate.exchange(requestEntity, Resource.class);
        } catch (RestClientException e) {
            logger.error(e.getMessage(), e);
            restTemplateException=e;
        }
        return response;
    }

    public RestClientException getRestTemplateException() {
        return restTemplateException;
    }
}
