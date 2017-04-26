package com.cooksys.airline;

import com.cooksys.airline.model.Airline;
import com.cooksys.airline.service.impl.AirlineServiceCsvImpl;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by justin on 3/9/17.
 */
public class AirlineServiceTest {

    @Test
    public void getAirlineById() throws Exception {
        AirlineServiceCsvImpl airlineService = new AirlineServiceCsvImpl();
        airlineService.init();
        assertNotNull("getAirlineById(1) should not return null", airlineService.getAirlineById(1));
    }

    @Test
    public void getAirlines() throws Exception {
        AirlineServiceCsvImpl airlineService = new AirlineServiceCsvImpl();
        airlineService.init();
        assertNotNull("getAirlines() should not return null", airlineService.getAirlines());
        assertTrue("getAirlines().size() should be greater than 0", airlineService.getAirlines().size() > 0);
    }

    @Test
    public void practiceMockitoTest() throws Exception {
        Airline airlineInfo = mock(Airline.class);
        when(airlineInfo.getCountry()).thenReturn("United States of America");

        assertEquals(airlineInfo.getCountry(), "United States of America");
    }

}