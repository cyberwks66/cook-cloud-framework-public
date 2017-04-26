package com.cooksys.cloud.commons.util;

import org.springframework.context.annotation.Bean;

/**
 * Configuration class that defines the utility class {@link SpringContext} as a spring managed bean.  This allows
 * easy access to spring's {@link org.springframework.context.ApplicationContext} from non-managed objects
 *
 * @author Tim Davidson
 */
public class CommonContextUtilConfiguration {
    @Bean
    public SpringContext springContext() {
        return new SpringContext();
    }
}
