package com.cooksys.cloud.commons.leaderelection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that should be included if a service needs to use leader election.  Defines spring beans
 * for leader election functionality
 *
 * @author Tim Davidson
 */
@Configuration
public class LeaderElectionConfiguration {
    @Bean
    public ElectionEndpoint electionEndpoint(LeaderElectionManager electionManager) {
        return new ElectionEndpoint(electionManager);
    }

    @Bean
    public LeaderElectionManager electionManager(ApplicationContext context) {
        return new LeaderElectionManager(context);
    }
}
