package com.cooksys.cloud.router.filters;

import com.cooksys.cloud.router.configuration.RouterProperties;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.router.core.TrafficRatioManager;
import com.cooksys.cloud.router.core.requestlog.RequestLogger;
import com.cooksys.cloud.router.filters.post.RequestLoggingFilter;
import com.cooksys.cloud.router.filters.pre.*;
import com.cooksys.cloud.router.filters.route.HttpClientFactory;
import com.cooksys.cloud.router.filters.route.LoadBalancedRoutingFilter;
import com.cooksys.cloud.router.filters.route.SimpleRoutingFilter;
import com.cooksys.cloud.router.filters.route.ribbon.VersionAwareRibbonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Spring bean auto-configuration of zuul filters
 *
 * @author Tim Davidson
 */
@Configuration
public class FilterConfiguration {

    public static final String EDGE_PROXY_MODE = "edgeProxyMode";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ROUTER = "router";

    @Value("${eureka.instance.hostName:default}")
    private String eurekaInstanceHostname;

    @Bean
    @ConditionalOnProperty(name = "router.edgeProxyMode", havingValue = TRUE)
    public PreDecorationFilter edgeProxyPreDecorationFilter(RouteManager routeManager,
                                                            RequestContextFactory contextFactory,
                                                            RouterProperties config) {
        return new EdgeProxyPreDecorationFilter(routeManager, contextFactory, config);
    }

    @Bean
    @ConditionalOnProperty(name = "router.edgeProxyMode", havingValue = FALSE)
    public PreDecorationFilter nodeProxyPreDecorationFilter(RouteManager routeManager,
                                                            RequestContextFactory contextFactory,
                                                            RouterProperties config) {
        return new NodeProxyPreDecorationFilter(routeManager, contextFactory, config);
    }

    @Bean
    public StopwatchFilter stopwatchFilter(RequestContextFactory contextFactory) {
        return new StopwatchFilter(contextFactory);
    }

    @Bean
    @ConditionalOnProperty(name = "router.edgeProxyMode", havingValue = TRUE)
    public RequestLoggingFilter requestLoggingFilter(RequestContextFactory contextFactory, RequestLogger requestLogger) {
        return new RequestLoggingFilter(contextFactory,requestLogger);
    }

    @Bean
    public RestOperations restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public LoadBalancedRoutingFilter loadBalancedRoutingFilter(RequestContextFactory contextFactory,
                                                               VersionAwareRibbonClient versionAwareRibbonClient,
                                                               RestOperations restTemplate,
                                                               RouterProperties config) {
        return new LoadBalancedRoutingFilter(contextFactory,versionAwareRibbonClient,config,restTemplate,eurekaInstanceHostname);
    }

    @Bean
    public TrafficRatioFilter trafficRatioFilter(RequestContextFactory requestContextFactory, TrafficRatioManager trafficRatioManager, RouterProperties config) {
        return new TrafficRatioFilter(requestContextFactory, trafficRatioManager, config);
    }

    @Bean
    public SimpleRoutingFilter simpleRoutingFilter(RequestContextFactory contextFactory, HttpClientFactory clientFactory) {
        return new SimpleRoutingFilter(contextFactory,clientFactory);
    }

    @Bean
    public SimpleRouteLocatorFilter simpleRouteLocatorFilter(RequestContextFactory contextFactory, RouterProperties config) {
        return new SimpleRouteLocatorFilter(contextFactory,config);
    }
    @Bean
    public HttpClientFactory httpClientFactory() {
        return new HttpClientFactory();
    }


}
