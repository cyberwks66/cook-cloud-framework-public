package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.core.SemanticAccuracy;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.cooksys.cloud.router.filters.route.hystrix.HttpClientHystrixCommand;
import com.cooksys.cloud.router.filters.route.hystrix.HystrixMetadata;
import com.cooksys.cloud.router.filters.route.ribbon.VersionAwareRibbonClient;
import com.cooksys.cloud.router.util.ContextHelper;
import com.github.zafarkhaja.semver.ParseException;
import com.github.zafarkhaja.semver.Version;
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
import org.springframework.cloud.client.ServiceInstance;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Main Apache HTTP client routing filter.  Routes request using ribbon wrapped in hystrix command.
 *
 * @author Tim Davidson
 */
public class LoadBalancedApacheClientRoutingFilter extends ApacheClientRoutingFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancedApacheClientRoutingFilter.class);

    public static final String HTTP = "http";
    public static final String PROXY_HOST = "proxyHost";
    public static final String HTTPS = "https";

    private final VersionAwareRibbonClient ribbonClient;
    private final RouterProperties config;

    public LoadBalancedApacheClientRoutingFilter(RequestContextFactory contextFactory,
                                                 VersionAwareRibbonClient ribbonClient,
                                                 HttpClientFactory httpClientFactory,
                                                 RouterProperties config) {
        super(contextFactory, httpClientFactory);
        this.ribbonClient = ribbonClient;
        this.config = config;
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.LOAD_BALANCED_ROUTING_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.LOAD_BALANCED_ROUTING_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        final RequestContext context = contextFactory.getZuulRequestContext();
        return context.get(ContextConstants.ROUTE_SERVICE_ID) != null;
    }

    @Override
    public Object run() {
        logger.info("executing LoadBalancedApacheClientRoutingFilter");
        try {
            final RequestContext context = contextFactory.getZuulRequestContext();
            final String serviceId = (String) context.get(ContextConstants.ROUTE_SERVICE_ID);
            final Version semanticVersion = (Version) context.get(ContextConstants.ROUTE_VERSION);
            SemanticAccuracy accuracy = (SemanticAccuracy) context.get(ContextConstants.ROUTE_VERSION_ACCURACY);

            if (accuracy == null) {
                accuracy = SemanticAccuracy.MAJOR;
            }

            // Get the next service instance from the loadbalancer
            final ServiceInstance serviceInstance;

            if (context.get(ContextConstants.ROUTE_EXCLUDED_VERSIONS) != null) {
                final List<Version> excludedVersions = (List<Version>) context.get(ContextConstants.ROUTE_EXCLUDED_VERSIONS);
                serviceInstance = ribbonClient.getNextServerFromLoadBalancer(serviceId, semanticVersion, accuracy, excludedVersions);
            } else {
                serviceInstance = ribbonClient.getNextServerFromLoadBalancer(serviceId, semanticVersion, accuracy);
            }

            if (serviceInstance == null) {
                ContextHelper.errorResponse(context, 500, new RuntimeException("No route available for serviceId: "
                        + serviceId + " version: " + (semanticVersion != null ? semanticVersion.toString() : "not specified")));
                return null;
            }

            if (config.isEdgeProxyMode()) {
                if (serviceInstance.getMetadata() == null || serviceInstance.getMetadata().get(PROXY_HOST) == null) {
                    ContextHelper.errorResponse(context, 500, new RuntimeException("Unable to route to service: "
                            + serviceId
                            + " - Router is configured for Edge node, but service instance metadata does not contain key: 'proxyHost'"));
                    return null;
                }
            }

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

            // if routeUsingProxyHost is true, this means I am configured in edge mode
            // Route to the proxy instead of the container directly
            final String routeHost = config.isEdgeProxyMode() ?
                    serviceInstance.getMetadata().get(PROXY_HOST) : serviceInstance.getHost();
            String routePath = (String) context.get(ContextConstants.ROUTE_PATH);

            if (serviceInstance.getMetadata() == null || !serviceInstance.getMetadata().containsKey("version")) {
                ContextHelper.errorResponse(context, 500, new RuntimeException("Version metadata is missing from service entry"));
                return null;
            }

            Version serviceInstanceVersion;
            try {
                serviceInstanceVersion = Version.valueOf(serviceInstance.getMetadata().get("version"));
            } catch (IllegalArgumentException | ParseException e) {
                ContextHelper.errorResponse(context, 500, e);
                return null;
            }

            // In Edge proxy mode, prepend the proxy route path to the request path
            if (config.isEdgeProxyMode()) {
                @SuppressWarnings("unchecked") Map<String, Version> proxyRoutes = (Map<String, Version>) context.get(ContextConstants.ROUTE_PROXY_ROUTES);
                if (proxyRoutes == null || proxyRoutes.isEmpty()) {
                    ContextHelper.errorResponse(context, 500, new RuntimeException("Edge mode is active, but route is missing from the context"));
                    return null;
                }

                for (Map.Entry<String, Version> routeEntry : proxyRoutes.entrySet()) {
                    if (serviceInstanceVersion.equals(routeEntry.getValue())) {
                        routePath = routeEntry.getKey() + routePath;
                    }
                }
            }

            final URIBuilder uriBuilder = new URIBuilder()
                    .setScheme(serviceInstance.isSecure() ? HTTPS : HTTP)
                    .setHost(routeHost)
                    .setPort(serviceInstance.getPort())
                    .setPath(routePath)
                    .setCustomQuery(request.getQueryString());

            final String uri = uriBuilder.toString();

            final HttpHost host = new HttpHost(routeHost, serviceInstance.getPort());
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

            // forward the request wrapped in a hystrix circuit breaker

            final HystrixMetadata metadata = new HystrixMetadata(serviceId,
                    serviceInstanceVersion.toString(), serviceId);
            final HttpClientHystrixCommand hystrixCommand = new HttpClientHystrixCommand(metadata, httpclient, host, httpRequest, context);
            HttpResponse response = hystrixCommand.execute();

            setResponse(response);

        } catch (Exception e) {
            ContextHelper.errorResponse(contextFactory.getZuulRequestContext(), 500, e);
        }
        return null;
    }


}
