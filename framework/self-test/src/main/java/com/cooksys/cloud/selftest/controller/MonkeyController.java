package com.cooksys.cloud.selftest.controller;

import ch.qos.logback.classic.Logger;
import com.cooksys.cloud.selftest.monkey.CpuHogMonkey;
import com.cooksys.cloud.selftest.monkey.MemoryLeakMonkey;
import com.cooksys.cloud.selftest.exception.ForbiddenException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Self test API endpoints
 *
 * @author timd
 */
@RestController
@RequestMapping("/selftest")
public class MonkeyController {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    int cpuCores;
    private MemoryLeakMonkey memoryLeak;
    private final List<CpuHogMonkey> cpuHogThreads = new ArrayList<CpuHogMonkey>();


    @Value("${spring.application.name}")
    private String serviceId;

    @PostConstruct
    public void init() {
        cpuCores = Runtime.getRuntime().availableProcessors();
    }

    /**
     * Start a memory leak - service manager should kill this process, once it
     * has filled up the heap.
     *
     * @return
     */
    @RequestMapping(value = "memoryleak", method = RequestMethod.POST)
    public String startMemoryLeakMonkey() {
        if (memoryLeak == null) {
            memoryLeak = new MemoryLeakMonkey();
            Thread t = new Thread(memoryLeak);
            t.start();
            return "{ \"message\" : \"Memory leak test started.\" }";
        } else {
            throw new ForbiddenException("Memory leak test already in progress.");
        }
    }

    /**
     * Start chewing up CPU.
     * <p>
     * Gets the number of cores, and starts 2 threads for each, to ensure
     * maximum CPU usage. Service manager sould kill this process after some
     * time, because there are no requests coming in.
     *
     * @return
     */
    @RequestMapping(value = "cpuhog", method = RequestMethod.POST)
    public String startCpuHogMonkey() {
        if (cpuHogThreads.size() == 0) {
            for (int i = 0; i < cpuCores * 2; i++) {
                final CpuHogMonkey cpuHogThread = new CpuHogMonkey();
                cpuHogThread.start();
                cpuHogThreads.add(cpuHogThread);
            }
            return "{ \"message\" : \"CPU Hog test started\" }";
        } else {
            throw new ForbiddenException("CPU Hog test already in progress");
        }
    }

    /**
     * Stops the CPU hog test
     *
     * @return
     */
    @RequestMapping(value = "cpuhog", method = RequestMethod.DELETE)
    public String stopCpuHogMonkey() {
        if (cpuHogThreads.size() > 0) {
            final Iterator<CpuHogMonkey> it = cpuHogThreads.iterator();
            while (it.hasNext()) {
                final CpuHogMonkey thread = it.next();
                thread.stopThread();
                it.remove();
            }
            return "{ \"message\" : \"Stopping CPU Hog test\" }";
        } else {
            throw new ForbiddenException("CPU Hog test was not started.");
        }
    }

    /**
     * Starts a CPU hog, and sends an event to router, telling it to give
     * service-manager a fake high response time metric, simulating load
     *
     * @return
     */
    @RequestMapping(value = "simulate-load", method = RequestMethod.POST)
    public String startLoadSimulation() {
        startCpuHogMonkey();

        // TODO implement simulate load event using http request or whatever flavor of event bus we are using
//        SelfTestSimulateLoadEvent event = new SelfTestSimulateLoadEvent();
//        event.setServiceId(serviceId);
//        event.setAction(SelfTestSimulateLoadEvent.Action.START);
//        eventClient.publishEvent(event);

        return "{ \"message\" : \"Starting load-simulation test\" }";
    }

    /**
     * Stops the cpu hog and simulate-load test
     *
     * @return
     */
    @RequestMapping(value = "simulate-load", method = RequestMethod.DELETE)
    public String stopLoadSimulation() {
        stopCpuHogMonkey();

        // TODO implement delete simulate load event using http request or whatever flavor of event bus we are using
//        SelfTestSimulateLoadEvent event = new SelfTestSimulateLoadEvent();
//        event.setServiceId(serviceId);
//        event.setAction(SelfTestSimulateLoadEvent.Action.STOP);
//        eventClient.publishEvent(event);

        return "{ \"message\" : \"Stopped load-simulation test\" }";
    }

    /**
     * A simple rest endpoint for verifying that self-test is running and
     * reachable.
     *
     * @return
     */
    @RequestMapping(value = "rest", method = RequestMethod.GET)
    public String request() {

        return "{ \"message\" : \"Self-test API\" }";
    }

    @RequestMapping(value = "exception", method = RequestMethod.GET)
    public
    @ResponseBody
    String exception() {
        String nullString = null;

        logger.info("Next line of code contains a caught exception.");

        try {
            String ucaseString = nullString.toUpperCase();
        } catch (Exception e) {
            logger.error("caught exception:" + e.getMessage());
        }

        logger.info("Next line of code should cause an uncaught null pointer exception.");

        String ucaseString = nullString.toUpperCase();
        return "";
    }

}
