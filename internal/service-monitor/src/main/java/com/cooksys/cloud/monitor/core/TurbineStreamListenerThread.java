package com.cooksys.cloud.monitor.core;

import com.cooksys.cloud.monitor.event.ServiceMetricEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Listener thread that monitors the turbine stream and publishes {@link ServiceMetricEvent} in the context when
 * real metrics are found in the stream.
 *
 * @author Tim Davidson
 */
public class TurbineStreamListenerThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(TurbineStreamListenerThread.class);
    public static final String VERSION_DELIM = " v";
    public static final String NAME = "name";
    public static final String REQUEST_COUNT = "requestCount";
    public static final String ERROR_PERCENTAGE = "errorPercentage";
    public static final String REPORTING_HOSTS = "reportingHosts";

    private final InputStream turbineStream;
    private ApplicationContext context;

    private boolean shouldExit = false;

    public TurbineStreamListenerThread(InputStream turbineStream, ApplicationContext context) {
        this.turbineStream = turbineStream;
        this.context = context;
    }

    @Override
    public void run() {
        BufferedReader turbineReader = null;
        try {
            turbineReader = new BufferedReader(
                    new InputStreamReader(turbineStream));
            startListenerLoop(turbineReader);
        } catch (IOException e) {
            logger.error("Failed to read turbine inputstream - thread exiting", e);
            return;
        }
    }

    private void startListenerLoop(BufferedReader turbineReader) throws IOException {
        String inputLine;

        while ((inputLine = turbineReader.readLine()) != null && !Thread.interrupted()) {
            if (": ping".equals(inputLine) || "".equals(inputLine)) {
                continue;
            }

            final String pingJson = inputLine.substring(6);

            final Gson gson = new Gson();
            final Type type = new TypeToken<Map<String, Object>>() {
            }.getType();

            final Map<String, Object> metrics = gson.fromJson(pingJson, type);

            if (metrics.get(NAME) == null
                    || metrics.get(REQUEST_COUNT) == null
                    || metrics.get(ERROR_PERCENTAGE) == null
                    || metrics.get(REPORTING_HOSTS) == null) {
                continue;
            }

            final String serviceId = parseServiceId((String) metrics.get(NAME));
            if (serviceId == null) {
                logger.warn("unknown name format in hystrix ping data - ignoring");
                continue;
            }

            final String version = parseVersion((String) metrics.get(NAME));
            final long requestCount = ((Double) metrics.get(REQUEST_COUNT)).longValue();
            final double errorPercentage = (Double) metrics.get(ERROR_PERCENTAGE);
            final int reportingHosts = ((Double) metrics.get(REPORTING_HOSTS)).intValue();

            final ServiceMetricEvent metricEvent = new ServiceMetricEvent(this, serviceId, version, requestCount, errorPercentage, reportingHosts);

            context.publishEvent(metricEvent);

            logger.debug("published metric event in context: " + metricEvent);
        }

        if (Thread.interrupted()) {
            logger.info("Thread interrupted - closing stream reader");
        } else {
            logger.warn("reached the end of the input stream - thread exiting");
        }

        turbineReader.close();
    }

    private static String parseServiceId(String name) {
        String[] splitName = name.split(VERSION_DELIM);
        if (splitName == null || splitName.length != 2) {
            return null;
        }
        return splitName[0];
    }

    private static String parseVersion(String name) {
        String[] splitName = name.split(VERSION_DELIM);
        if (splitName == null || splitName.length != 2) {
            return null;
        }
        return splitName[1];
    }
}
