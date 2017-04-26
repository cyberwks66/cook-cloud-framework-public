package com.cooksys.cloud.commons.event;

import com.cooksys.cloud.commons.util.SpringContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Spring Bus to scan for events in this package (RemoteApplicationEventScan annotation)
 *
 * @author Tim Davidson
 */
@Configuration
@RemoteApplicationEventScan
public class EventConfiguration {

    @Bean
    @ConditionalOnMissingBean(SpringContext.class)
    public SpringContext springContext() {
        return new SpringContext();
    }
}
