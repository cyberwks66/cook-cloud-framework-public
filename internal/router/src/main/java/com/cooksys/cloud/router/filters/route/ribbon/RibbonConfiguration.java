package com.cooksys.cloud.router.filters.route.ribbon;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Spring bean definition for custom configured Ribbon client
 *
 * @author Tim Davidson
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public VersionAwareRibbonClient metadataAwareRibbonClient(LoadBalancerClient ribbonClient) {
        return new VersionAwareRibbonClient(ribbonClient);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DiscoveryEnabledRule metadataAwareRule() {
        return new VersionAwareRule();
    }
}
