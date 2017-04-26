package com.cooksys.airline.controller;

import com.cooksys.airline.model.Airline;
import com.cooksys.airline.service.AirlineService;
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
@RequestMapping("/airlines")
@RestController
public class AirlineController {
    public final AirlineService airlineService;

    @Value("${eureka.instance.metadataMap.version:default}")
    private String versionMetadata;

    @Autowired
    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @RequestMapping(value="/",method= RequestMethod.GET)
    public Map<String, Object> airlineInfo() {
        final Map<String, Object> responseBody = new HashMap<>();
        List<Airline> airlineInfoList = airlineService.getAirlines();

        responseBody.put("version", versionMetadata);
        responseBody.put("airlines", airlineInfoList);
        return responseBody;
    }

    @RequestMapping(value="/countries/{airlineCountry}",method= RequestMethod.GET)
    public Map<String, Object> airlineInfoByCountry(@PathVariable(value = "airlineCountry") String country) {
        final Map<String, Object> responseBody = new HashMap<>();
        List<Airline> airlineInfoList = airlineService.getAirlinesByCountry(country);

        responseBody.put("version", versionMetadata);
        responseBody.put("airports", airlineInfoList);
        return responseBody;
    }

    @RequestMapping(value="/iata/{airlineIata}",method= RequestMethod.GET)
    public Map<String, Object> airlineInfoByIata(@PathVariable(value = "airlineIata") String iata) {
        final Map<String, Object> responseBody = new HashMap<>();
        Airline airlineInfo = airlineService.getAirlineByIata(iata);

        responseBody.put("version", versionMetadata);
        responseBody.put("airlines", airlineInfo);
        return responseBody;
    }

    @RequestMapping(value="/icao/{airlineIcao}",method= RequestMethod.GET)
    public Map<String, Object> airlineInfoByIcao(@PathVariable(value = "airlineIcao") String icao) {
        final Map<String, Object> responseBody = new HashMap<>();
        Airline airlineInfo = airlineService.getAirlineByIcao(icao);

        responseBody.put("version", versionMetadata);
        responseBody.put("airlines", airlineInfo);
        return responseBody;
    }
}
