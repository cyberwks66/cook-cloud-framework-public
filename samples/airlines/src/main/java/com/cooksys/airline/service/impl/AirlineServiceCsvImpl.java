package com.cooksys.airline.service.impl;

import com.cooksys.airline.model.Airline;
import com.cooksys.airline.service.AirlineService;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by justin on 3/8/17.
 */
public class AirlineServiceCsvImpl implements AirlineService {

    private static Map<Long, Airline> airlineInfoMapById;
    private static Map<String, Airline> airlineInfoMapByIata;
    private static Map<String, Airline> airlineInfoMapByIcao;


    @PostConstruct
    public void init() {
        InputStream in = getClass().getResourceAsStream("/airlines.dat");
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

        System.out.println("Airline Service init()");
        airlineInfoMapById = new HashMap<>();
        airlineInfoMapByIata = new HashMap<>();
        airlineInfoMapByIcao = new HashMap<>();

        for (String line : lines) {
            line = line.replaceAll("[\"]", "");
            Airline airlineInfo = populateAirlineData(line);
            airlineInfoMapById.put(airlineInfo.getAirlineId(), airlineInfo);
            airlineInfoMapByIata.put(airlineInfo.getIata(), airlineInfo);
            airlineInfoMapByIcao.put(airlineInfo.getIcao(), airlineInfo);
        }
    }

    private Airline populateAirlineData(String line) {
        Airline airlineInfo = new Airline();
        String[] data = line.split(",");
        airlineInfo.setAirlineId(Long.parseLong(data[0]));
        airlineInfo.setName(data[1]);
        airlineInfo.setAlias(data[2]);
        airlineInfo.setIata(data[3]);
        airlineInfo.setIcao(data[4]);
        airlineInfo.setCallsign(data[5]);
        airlineInfo.setCountry(data[6]);
        airlineInfo.setActive(data[7]);
        return airlineInfo;
    }

    @Override
    public Airline getAirlineById(long id) {
        return airlineInfoMapById.get(id);
    }

    @Override
    public Airline getAirlineByIata(String iata) {
        iata = iata.toUpperCase();
        return airlineInfoMapByIata.get(iata);
    }

    @Override
    public Airline getAirlineByIcao(String icao) {
        icao = icao.toUpperCase();
        return airlineInfoMapByIcao.get(icao);
    }

    @Override
    public List<Airline> getAirlines() {
        return new ArrayList(airlineInfoMapById.values());
    }

    @Override
    public List<Airline> getAirlinesByCountry(String country) {
        List<Airline> airlineInfoList = new ArrayList<>();
        if (country == null) {
            return airlineInfoList;
        }
        country = country.replaceAll("[_-]", " ");
        for (Airline airlineInfo : airlineInfoMapById.values()) {
            if (country.equalsIgnoreCase(airlineInfo.getCountry())) {
                airlineInfoList.add(airlineInfo);
            }
        }
        return airlineInfoList;
    }
}
