package com.cooksys.cloud.selftest.controller;

import com.cooksys.cloud.selftest.bus.BusTestResponse;
import com.cooksys.cloud.selftest.bus.BusTestService;
import com.cooksys.cloud.selftest.bus.DiscoveryClientService;
import com.cooksys.cloud.selftest.bus.RemoteEventTestSequence;
import com.cooksys.cloud.selftest.exception.InternalServerErrorException;
import com.cooksys.cloud.selftest.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Self-test for verifying the bus is working properly.  There are 2 endpoints - one for initiating the BusTestRemoteApplicationEvent,
 * and one to verify that all nodes have published the BusTestAckRemoteApplicationEvent
 */
@RestController
public class BusTestController {
    private static final Logger logger = LoggerFactory.getLogger(BusTestController.class);

    private DiscoveryClientService discovery;
    private BusTestService service;

    @Autowired
    public BusTestController(DiscoveryClientService discovery, BusTestService service) {
        this.discovery = discovery;
        this.service = service;
    }

    @RequestMapping(value = "/busTest", method = RequestMethod.POST)
    public BusTestResponse initiateBusTest() {
        final BusTestResponse response = new BusTestResponse();

        final Optional<List<String>> instances = discovery.getAvailableSelftestInstanceIds();
        if (instances.isPresent()) {
            response.setDiscoverySelftestInstances(instances.get());
        } else {
            throw new InternalServerErrorException("Unable to fetch discovery info - no self-test instances present in local cache.  If you just started an instance of self-test, you may need to wait for up to 2 minutes for all discovery caches to get in sync");
        }

        // publish the event to all self-test instances
        String uuid = service.initiateTestSequence();
        response.setTestId(uuid);

        return response;
    }

    @RequestMapping(value = "/busTest/{testId}", method = RequestMethod.GET)
    public RemoteEventTestSequence getBusTest(@PathVariable String testId) {
        Optional<RemoteEventTestSequence> storedTest = null;
        try {
            storedTest = service.getTestResults(testId);
        } catch (RuntimeException e) {
            throw new InternalServerErrorException(e.getMessage());
        }

        if (!storedTest.isPresent()) {
            throw new NotFoundException("BusTest with ID " + testId + " was not found.  (Service only stores the last 10 Tests)");
        }
        return storedTest.get();
    }
}
