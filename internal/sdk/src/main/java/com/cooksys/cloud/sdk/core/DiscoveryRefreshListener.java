package com.cooksys.cloud.sdk.core;

import ch.qos.logback.classic.Logger;
import com.netflix.discovery.EurekaClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collection;

import static com.cooksys.cloud.commons.SharedConstants.DISCOVERY_CLIENT_ZONE;
import static com.cooksys.cloud.commons.SharedConstants.DISCOVERY_SERVER_URL_MAX_COUNT;

/**
 * Event handler for every time discovery client refreshes it's cache. Here we
 * can log up/down events as well as tell the config client to request the
 * configuration from the config server
 *
 * @author Tim Davidson
 */
@Configuration
@Import({EurekaClientAutoConfiguration.class})
public class DiscoveryRefreshListener implements ApplicationListener<HeartbeatEvent> {

    @Autowired
    private EurekaClient discoveryClient;



    @Autowired
    private EurekaClientConfigBean config;

    @Value("${server.port}")
    private String serverPort;

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(HeartbeatEvent event) {

        // get the top 3 discovery servers, and make sure we have 2 backups
        // configured in case the one we registered with goes down

        final Collection<String> discoveryUrls = config.getServiceUrl().values();

        logger.info("discovery URLS" + discoveryUrls);

        int serverCount = 0;
        String defaultZone = "";

        for (final String discoveryUrl : discoveryUrls) {
            defaultZone += (serverCount > 0 ? ("," + discoveryUrl) : discoveryUrl);
            serverCount++;
            if (serverCount == DISCOVERY_SERVER_URL_MAX_COUNT) {
                break;
            }
        }

        config.getServiceUrl().put(DISCOVERY_CLIENT_ZONE, defaultZone);

    }

}
