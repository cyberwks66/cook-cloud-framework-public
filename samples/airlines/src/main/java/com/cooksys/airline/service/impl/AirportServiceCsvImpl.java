package com.cooksys.airline.service.impl;

import com.cooksys.airline.model.Airport;
import com.cooksys.airline.service.AirportService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by justin on 3/8/17.
 */
public class AirportServiceCsvImpl implements AirportService {

    private static Map<Long, Airport> airportInfoMapById;
    private static Map<String, Airport> airportInfoMapByIata;
    private static Map<String, Airport> airportInfoMapByIcao;

    public void init() {
        InputStream in = getClass().getResourceAsStream("/airports.dat");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String inline;
        List<String> lines = new ArrayList<>();
        try {
            while ((inline = reader.readLine()) != null) {
                lines.add(inline);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        airportInfoMapById = new HashMap<>();
        airportInfoMapByIata = new HashMap<>();
        airportInfoMapByIcao = new HashMap<>();
        Set<String> set = new HashSet<>();

        for (String line : lines) {
            line = line.replaceAll("[\"]", "");
            Airport airport = populateAirportInfo(line);
            airportInfoMapById.put(airport.getAirportId(), airport);
            airportInfoMapByIata.put(airport.getIata(), airport);
            airportInfoMapByIcao.put(airport.getIcao(), airport);
        }

    }

    private Airport populateAirportInfo(String line) {
        Airport airport = new Airport();
        String[] data = line.split(",");
        airport.setAirportId(Long.parseLong(data[0]));
        airport.setName(data[1]);
        airport.setCity(data[2]);
        airport.setCountry(data[3]);
        airport.setIata(data[4]);
        airport.setIcao(data[5]);
        airport.setLatitude(data[6]);
        airport.setLongitude(data[7]);
        airport.setAltitude(data[8]);
        airport.setTimezone(data[9]);
        airport.setDst(data[10]);
        airport.setDbTimezone(data[11]);
        airport.setType(data[12]);
        airport.setSource(data[13]);
        return airport;
    }

    @Override
    public Airport getAirportById(long id) {
        return airportInfoMapById.get(id);
    }

    @Override
    public Airport getAirportByIata(String iata) {
        iata = iata.toUpperCase();
        return airportInfoMapByIata.get(iata);
    }

    @Override
    public Airport getAirportByIcao(String icao) {
        icao = icao.toUpperCase();
        return airportInfoMapByIcao.get(icao);
    }

    @Override
    public List<Airport> getAirports() {
        return new ArrayList(airportInfoMapById.values());
    }

    @Override
    public List<Airport> getAirportsByCountry(String country) {
        List<Airport> airportList = new ArrayList<>();
        if (country == null) {
            return airportList;
        }
        country = country.replaceAll("[_-]", " ");
        for (Airport airport : airportInfoMapById.values()) {
            if (country.equalsIgnoreCase(airport.getCountry())) {
                airportList.add(airport);
            }
        }
        return airportList;
    }

    @Override
    public List<Airport> getAirportsByCity(String city) {
        List<Airport> airportList = new ArrayList<>();
        if (city == null) {
            return airportList;
        }
        city = city.replaceAll("[_-]", " ");
        for (Airport airport : airportInfoMapById.values()) {
            if (city.equalsIgnoreCase(airport.getCity())) {
                airportList.add(airport);
            }
        }
        return airportList;
    }
}
