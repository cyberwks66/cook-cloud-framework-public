package com.cooksys.airline.controller;

import com.cooksys.airline.model.Airport;
import com.cooksys.airline.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by timd on 3/24/17.
 */
@RestController
@RequestMapping("/airports")
public class AirportController {
    private AirportService airportService;

    @Value("${eureka.instance.metadataMap.version:0.1.0}")
    private String versionMetadata;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @RequestMapping(value="/",method= RequestMethod.GET)
    public Map<String, Object> airportInfo() {
        final Map<String, Object> responseBody = new HashMap<>();
        List<Airport> airportList = airportService.getAirports();

        responseBody.put("version", versionMetadata);
        responseBody.put("airports", airportList);
        return responseBody;
    }

    @RequestMapping(value="/countries/{airportCountry}",method=RequestMethod.GET)
    public Map<String, Object> airportInfoByCountry(@PathVariable(value = "airportCountry") String country) {
        final Map<String, Object> responseBody = new HashMap<>();
        List<Airport> airportList = airportService.getAirportsByCountry(country);

        responseBody.put("version", versionMetadata);
        responseBody.put("airports", airportList);
        return responseBody;
    }

    @RequestMapping(value="/cities/{airportCity}",method=RequestMethod.GET)
    public Map<String, Object> airportInfoByCity(@PathVariable(value = "airportCity") String city) {
        final Map<String, Object> responseBody = new HashMap<>();
        List<Airport> airportList = airportService.getAirportsByCity(city);

        responseBody.put("version", versionMetadata);
        responseBody.put("airports", airportList);
        return responseBody;
    }

    @RequestMapping(value="/iata/{airportIata}",method=RequestMethod.GET)
    public Map<String, Object> airportInfoByIata(@PathVariable(value = "airportIata") String iata) {
        final Map<String, Object> responseBody = new HashMap<>();
        Airport airport = airportService.getAirportByIata(iata);

        responseBody.put("version", versionMetadata);
        responseBody.put("airports", airport);
        return responseBody;
    }

    @RequestMapping(value="/icao/{airportIcao}",method=RequestMethod.GET)
    public Map<String, Object> airportInfoByIcao(@PathVariable(value = "airportIcao") String icao) {
        final Map<String, Object> responseBody = new HashMap<>();
        Airport airport = airportService.getAirportByIcao(icao);

        responseBody.put("version", versionMetadata);
        responseBody.put("airports", airport);
        return responseBody;
    }
}
