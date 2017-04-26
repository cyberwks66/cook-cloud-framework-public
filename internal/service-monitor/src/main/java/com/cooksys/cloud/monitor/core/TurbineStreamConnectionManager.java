package com.cooksys.cloud.monitor.core;

import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Manages the initial connection to Turbine stream, and re-establishes the connection if it is lost.
 *
 * @author Tim Davidson
 */
@EnableScheduling
public class TurbineStreamConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(TurbineStreamConnectionManager.class);

    public static final String TURBINE = "turbine";
    public static final String GET = "GET";
    public static final String USER_AGENT = "User-Agent";
    public static final String MOZILLA_5_0 = "Mozilla/5.0";
    public static final int OK_200 = 200;

    private final DiscoveryClient discoveryClient;
    private final LeaderElectionManager electionManager;
    private final ApplicationContext context;

    private InputStream turbineInputStream;
    private Thread turbineStreamListenerThread;


    public TurbineStreamConnectionManager(DiscoveryClient discoveryClient, LeaderElectionManager electionManager, ApplicationContext context) {
        this.discoveryClient = discoveryClient;
        this.electionManager = electionManager;
        this.context = context;
    }

    @Scheduled(fixedDelay = 10_000)
    public void monitorElectionResults() {
        if (electionManager.isLeader()) {
            if (turbineStreamListenerThread == null || !turbineStreamListenerThread.isAlive()) {
                try {
                    refreshTurbineInputStream();
                } catch (IOException | RuntimeException e) {
                    logger.error("unable to connect to turbine", e);
                    return;
                }
                turbineStreamListenerThread = new TurbineStreamListenerThread(turbineInputStream, context);
                turbineStreamListenerThread.start();
            }
        } else {
            if (turbineStreamListenerThread != null && turbineStreamListenerThread.isAlive()) {
                turbineStreamListenerThread.interrupt();
                closeTurbineStream();
            }
        }
    }

    private void refreshTurbineInputStream() throws RuntimeException, IOException {
        closeTurbineStream();

        final List<ServiceInstance> turbineInstances = discoveryClient.getInstances(TURBINE);
        if (turbineInstances == null || turbineInstances.isEmpty()) {
            throw new RuntimeException("Could not find any turbine instances registered with eureka");
        }

        final URL turbineURL = new URL(turbineInstances.get(0).getUri().toString() + "/turbine.stream");
        final HttpURLConnection turbineConnection = (HttpURLConnection) turbineURL.openConnection();

        logger.debug("Connecting to turbine stream at " + turbineURL.toString());

        turbineConnection.setRequestMethod(GET);
        turbineConnection.setRequestProperty(USER_AGENT, MOZILLA_5_0);

        int responseCode = turbineConnection.getResponseCode();

        if (responseCode != OK_200) {
            throw new RuntimeException("Could not connect to stream.  Status: " + responseCode);
        }
        turbineInputStream = turbineConnection.getInputStream();
    }

    public void closeTurbineStream() {
        if (turbineInputStream != null) {
            try {
                turbineInputStream.close();
            } catch (IOException e) {

            }
        }
    }
}
