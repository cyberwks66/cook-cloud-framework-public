package com.cooksys.airline.service;

import com.cooksys.airline.model.Airline;

import java.util.List;

/**
 * Created by timd on 3/24/17.
 */
public interface AirlineService {
    Airline getAirlineById(long id);

    Airline getAirlineByIata(String iata);

    Airline getAirlineByIcao(String icao);

    List<Airline> getAirlines();

    List<Airline> getAirlinesByCountry(String country);
}
