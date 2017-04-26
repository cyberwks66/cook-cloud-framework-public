package com.cooksys.cloud.router.configuration;

import com.cooksys.cloud.commons.event.EventConfiguration;
import com.cooksys.cloud.commons.util.EnableStaticContext;
import com.cooksys.cloud.router.core.requestlog.ElasticsearchRequestLogger;
import com.cooksys.cloud.router.core.requestlog.RequestLogger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Configuration class for router Application
 *
 * @author Tim Davidson
 */
@Configuration
@Import(EventConfiguration.class)
@EnableStaticContext
public class RouterConfiguration {

    public static final String ROUTER = "router";
    public static final String ROUTE_ID = "global";
    public static final String GLOBAL_MAPPING = "/**";
    public static final String LOCATION = "";
    public static final String PREFIX = "";

    private Client elasticsearchClient;

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(@Value("${server.port}") final String port,
                                                                                     @Value("${jetty.threadPool.maxThreads:1000}") final String maxThreads,
                                                                                     @Value("${jetty.threadPool.minThreads:8}") final String minThreads,
                                                                                     @Value("${jetty.threadPool.idleTimeout:60000}") final String idleTimeout) {
        final JettyEmbeddedServletContainerFactory factory =  new JettyEmbeddedServletContainerFactory(Integer.valueOf(port));
        factory.addServerCustomizers(new JettyServerCustomizer() {
            @Override
            public void customize(final Server server) {
                // Tweak the connection pool used by Jetty to handle incoming HTTP connections
                final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
                threadPool.setMaxThreads(Integer.valueOf(maxThreads));
                threadPool.setMinThreads(Integer.valueOf(minThreads));
                threadPool.setIdleTimeout(Integer.valueOf(idleTimeout));
            }
        });
        return factory;
    }

    @Bean
    @ConfigurationProperties(prefix = ROUTER)
    public RouterProperties routerProperties() {
        return new RouterProperties();
    }

    /**
     * Configures zuul to pass all incoming requests through the filter chain
     *
     * @return
     */
    @Bean
    public RouteLocator routeLocator() {
        return new RouteLocator() {
            @Override
            public Collection<String> getIgnoredPaths() {
                return new HashSet<>();
            }

            @Override
            public List<Route> getRoutes() {
                final Route globalRoute = new Route(ROUTE_ID, GLOBAL_MAPPING, LOCATION, PREFIX, true, null);
                final List<Route> routes = new ArrayList<>();
                routes.add(globalRoute);
                return routes;
            }

            @Override
            public Route getMatchingRoute(String s) {
                return null;
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = "router.edgeProxyMode", havingValue = "true")
    public Client elasticsearchClient(@Value("${elasticsearchLoggingAppender.host}") String elasticsearchHost,
                                      @Value("${elasticsearchLoggingAppender.port}") int elasticsearchPort) throws UnknownHostException {
        this.elasticsearchClient= new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticsearchHost), elasticsearchPort));
        return elasticsearchClient;
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecuter() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(50);

        return taskExecutor;
    }

    @Bean
    @ConditionalOnProperty(name = "router.edgeProxyMode", havingValue = "true")
    public RequestLogger requestLogger(Client elasticsearchClient, ThreadPoolTaskExecutor taskExecutor) {
        return new ElasticsearchRequestLogger(elasticsearchClient,taskExecutor);
    }

    @PreDestroy
    public void closeConnections() {
        if(elasticsearchClient!=null) {
            elasticsearchClient.close();
        }
    }

}
