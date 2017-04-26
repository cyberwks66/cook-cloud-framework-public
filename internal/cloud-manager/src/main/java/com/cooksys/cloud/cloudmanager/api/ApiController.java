package com.cooksys.cloud.cloudmanager.api;

import com.cooksys.cloud.cloudmanager.core.ServiceStateManager;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.ScaledownServiceBusEvent;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleupServiceBusEvent;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility API for testing cloud-manager
 *
 * Includes 2 endpoints that publish scale-up and scale down events on the bus to simulate the events normally published
 * by service-monitor, and a services endpoint to inspect the current scale states of cluster and services
 *
 * @author Tim Davidson
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private ApplicationContext context;
    private ServiceStateManager stateManager;

    @Autowired
    public ApiController(ApplicationContext context, ServiceStateManager stateManager) {
        this.context = context;
        this.stateManager = stateManager;
    }

    @RequestMapping(value = "/services/scaleup", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> scaleupService(@RequestBody @Valid ServiceRequest serviceRequest) {
        final Map<String, Object> response = new LinkedHashMap<>();

        final ScaleupServiceBusEvent event =
                new ScaleupServiceBusEvent(this, serviceRequest.getServiceId(), serviceRequest.getVersion(),serviceRequest.getInstances());

        context.publishEvent(event);

        response.put("statusCode", "200");
        response.put("statusDesc", "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/services/scaledown", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> scaledownService(@RequestBody @Valid ServiceRequest serviceRequest) {
        final Map<String, Object> response = new LinkedHashMap<>();

        final ScaledownServiceBusEvent event =
                new ScaledownServiceBusEvent(this, serviceRequest.getServiceId(), serviceRequest.getVersion(), serviceRequest.getInstances());

        context.publishEvent(event);

        response.put("statusCode", "200");
        response.put("statusDesc", "Success");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/services", method=RequestMethod.GET)
    public Map<Service,ScaleState> getStates() {
        return stateManager.getServiceStateMapCopy();
    }

}
