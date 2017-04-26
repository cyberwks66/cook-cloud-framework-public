package com.cooksys.airline.service;

import com.cooksys.airline.model.Airport;

import java.util.List;

/**
 * Created by timd on 3/24/17.
 */
public interface AirportService {
    Airport getAirportById(long id);

    Airport getAirportByIata(String iata);

    Airport getAirportByIcao(String icao);

    List<Airport> getAirports();

    List<Airport> getAirportsByCountry(String country);

    List<Airport> getAirportsByCity(String city);
}
