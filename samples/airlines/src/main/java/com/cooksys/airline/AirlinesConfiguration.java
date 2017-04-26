package com.cooksys.airline;

import com.cooksys.airline.service.AirlineService;
import com.cooksys.airline.service.impl.AirlineServiceCsvImpl;
import com.cooksys.airline.service.AirportService;
import com.cooksys.airline.service.impl.AirportServiceCsvImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by timd on 3/8/17.
 */
@Configuration
public class AirlinesConfiguration {
    @Bean(initMethod = "init")
    public AirportService airportService() {
        return new AirportServiceCsvImpl();
    }

    @Bean(initMethod = "init")
    public AirlineService airlineService() {
        return new AirlineServiceCsvImpl();
    }
}
