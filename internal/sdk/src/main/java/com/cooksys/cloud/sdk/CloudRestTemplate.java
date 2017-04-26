package com.cooksys.cloud.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

/**
 * Wrapper around Spring's RestTemplate that accepts serviceId + resourcePath
 * instead of URL. Ensures that HTTP requests to other services on the Cloud
 * platform are load balanced, and contain a circuit breaker. Also allows
 * automatic gathering of response time metrics that is fed to the auto-scale
 * algorithm.
 *
 * TODO: we need to update these methods to accept version arguments
 *
 * @author Tim Davidson
 */
@Component
public class CloudRestTemplate {

    @Autowired
    private RestTemplate restTemplate;

    // GET

    /**
     * Sends a GET request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param responseType
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public <T> T getForObject(String serviceId, String resourcePath, Class<T> responseType, Object... urlVariables)
            throws RestClientException {
        return restTemplate.getForObject(buildUrl(serviceId, resourcePath), responseType, urlVariables);
    }

    /**
     * Sends a GET request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param responseType
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public <T> T getForObject(String serviceId, String resourcePath, Class<T> responseType, Map<String, ?> urlVariables)
            throws RestClientException {
        return restTemplate.getForObject(buildUrl(serviceId, resourcePath), responseType, urlVariables);
    }

    /**
     * Sends a GET request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param responseType
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> getForEntity(String serviceId, String resourcePath, Class<T> responseType,
                                              Object... urlVariables) throws RestClientException {
        return restTemplate.getForEntity(buildUrl(serviceId, resourcePath), responseType, urlVariables);
    }

    /**
     * Sends a GET request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param responseType
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> getForEntity(String serviceId, String resourcePath, Class<T> responseType,
                                              Map<String, ?> urlVariables) throws RestClientException {
        return restTemplate.getForEntity(buildUrl(serviceId, resourcePath), responseType, urlVariables);
    }

    // HEAD

    /**
     * Sends a HEAD request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public HttpHeaders headForHeaders(String serviceId, String resourcePath, Object... urlVariables)
            throws RestClientException {
        return restTemplate.headForHeaders(buildUrl(serviceId, resourcePath), urlVariables);
    }

    /**
     * Sends a HEAD request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public HttpHeaders headForHeaders(String serviceId, String resourcePath, Map<String, ?> urlVariables)
            throws RestClientException {
        return restTemplate.headForHeaders(buildUrl(serviceId, resourcePath), urlVariables);
    }

    // POST

    /**
     * Sends a POST request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param request
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> T postForObject(String serviceId, String resourcePath, Object request, Class<T> responseType,
                               Object... uriVariables) throws RestClientException {
        return restTemplate.postForObject(buildUrl(serviceId, resourcePath), request, responseType, uriVariables);
    }

    /**
     * Sends a POST request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param request
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> T postForObject(String serviceId, String resourcePath, Object request, Class<T> responseType,
                               Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForObject(buildUrl(serviceId, resourcePath), request, responseType, uriVariables);
    }

    /**
     * Sends a POST request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param request
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> postForEntity(String serviceId, String resourcePath, Object request,
                                               Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.postForEntity(buildUrl(serviceId, resourcePath), request, responseType, uriVariables);
    }

    /**
     * Sends a POST request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param request
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> postForEntity(String serviceId, String resourcePath, Object request,
                                               Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForEntity(buildUrl(serviceId, resourcePath), request, responseType, uriVariables);
    }

    // PUT

    /**
     * Sends a PUT request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param request
     * @param urlVariables
     * @throws RestClientException
     */
    public void put(String serviceId, String resourcePath, Object request, Object... urlVariables)
            throws RestClientException {
        restTemplate.put(buildUrl(serviceId, resourcePath), request, urlVariables);
    }

    /**
     * Sends a PUT request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param request
     * @param urlVariables
     * @throws RestClientException
     */
    public void put(String serviceId, String resourcePath, Object request, Map<String, ?> urlVariables)
            throws RestClientException {
        restTemplate.put(buildUrl(serviceId, resourcePath), request, urlVariables);
    }

    // DELETE

    /**
     * Sends a DELETE request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param urlVariables
     * @throws RestClientException
     */
    public void delete(String serviceId, String resourcePath, Object... urlVariables) throws RestClientException {
        restTemplate.delete(buildUrl(serviceId, resourcePath), urlVariables);
    }

    /**
     * Sends a DELETE request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param urlVariables
     * @throws RestClientException
     */
    public void delete(String serviceId, String resourcePath, Map<String, ?> urlVariables) throws RestClientException {
        restTemplate.delete(buildUrl(serviceId, resourcePath), urlVariables);
    }

    // OPTIONS

    /**
     * Sends a OPTIONS request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public Set<HttpMethod> optionsForAllow(String serviceId, String resourcePath, Object... urlVariables)
            throws RestClientException {
        return restTemplate.optionsForAllow(buildUrl(serviceId, resourcePath), urlVariables);
    }

    /**
     * Sends a OPTIONS request to a service running on the framework
     *
     * @param serviceId
     * @param resourcePath
     * @param urlVariables
     * @return
     * @throws RestClientException
     */
    public Set<HttpMethod> optionsForAllow(String serviceId, String resourcePath, Map<String, ?> urlVariables)
            throws RestClientException {
        return restTemplate.optionsForAllow(buildUrl(serviceId, resourcePath), urlVariables);
    }

    // exchange

    /**
     * Generalized request that be used to support additional, less frequent combinations
     *
     * @param serviceId
     * @param resourcePath
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> exchange(String serviceId, String resourcePath, HttpMethod method,
                                          HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.exchange(buildUrl(serviceId, resourcePath), method, requestEntity, responseType,
                uriVariables);
    }

    /**
     * Generalized request that be used to support additional, less frequent combinations
     *
     * @param serviceId
     * @param resourcePath
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> exchange(String serviceId, String resourcePath, HttpMethod method,
                                          HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
            throws RestClientException {
        return restTemplate.exchange(buildUrl(serviceId, resourcePath), method, requestEntity, responseType,
                uriVariables);
    }

    /**
     * Generalized request that be used to support additional, less frequent combinations
     *
     * @param serviceId
     * @param resourcePath
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> exchange(String serviceId, String resourcePath, HttpMethod method,
                                          HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables)
            throws RestClientException {
        return restTemplate.exchange(buildUrl(serviceId, resourcePath), method, requestEntity, responseType,
                uriVariables);
    }

    /**
     * Generalized request that be used to support additional, less frequent combinations
     *
     * @param serviceId
     * @param resourcePath
     * @param method
     * @param requestEntity
     * @param responseType
     * @param uriVariables
     * @return
     * @throws RestClientException
     */
    public <T> ResponseEntity<T> exchange(String serviceId, String resourcePath, HttpMethod method,
                                          HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables)
            throws RestClientException {
        return restTemplate.exchange(buildUrl(serviceId, resourcePath), method, requestEntity, responseType,
                uriVariables);
    }

    private String buildUrl(String serviceId, String resourcePath) {
        String url = "http://router/" + serviceId;
        if (resourcePath != null) {
            url += (resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath);
        }
        return url;
    }
}
