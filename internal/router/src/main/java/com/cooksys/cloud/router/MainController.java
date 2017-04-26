package com.cooksys.cloud.router;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main REST controller for router application
 *
 * @author Tim Davidson
 */
@RestController
public class MainController {

    @Autowired
    private EurekaClient discoveryClient;

    /**
     * Returns a map of application name/instance count for services registered
     * with eureka
     *
     * @return
     */
    @RequestMapping(value = "/discovery-client/instances", method = RequestMethod.GET)
    public Map<String, Integer> getAllInstances() {
        final Map<String, Integer> response = new HashMap<>();

        final Applications applications = discoveryClient.getApplications();
        final List<Application> applicationList = applications.getRegisteredApplications();

        //TODO Java 8 Stream
        // Get instances from eureka
        for (final Application app : applicationList) {
            response.put(app.getName(), app.getInstances().size());
        }

        return response;
    }



}