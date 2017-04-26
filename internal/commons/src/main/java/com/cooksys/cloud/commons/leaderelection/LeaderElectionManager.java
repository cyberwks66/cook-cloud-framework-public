package com.cooksys.cloud.commons.leaderelection;

import com.cooksys.cloud.commons.event.election.ElectionHeartbeatBusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sends and receives leader election events and determines which instance is leader by sorting UUIDs of each instance
 *
 * @author Tim Davidson
 */
@EnableScheduling
public class LeaderElectionManager implements ApplicationListener<ElectionHeartbeatBusEvent> {
    private static final Logger logger = LoggerFactory.getLogger(LeaderElectionManager.class);
    public static final int INSTANCE_HEARTBEAT_TIMEOUT = 15_000;
    public static final int INSTANCE_HEARTBEAT_INTERVAL = 10_000;

    static String myUuid = UUID.randomUUID().toString();

    @Value("${spring.application.name:default}")
    private String serviceId;

    // Map of uuids that have been recieved from bus events - value is timestamp of last heartbeat
    Map<String, LocalDateTime> clusterHeartbeatRegistry = new HashMap<>();

    private ApplicationContext context;

    public LeaderElectionManager(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Override
    public void onApplicationEvent(ElectionHeartbeatBusEvent event) {
        //logger.debug("received ElectionHeartbeatBusEvent uuid: " + event.getUuid());
        // ignore this event if it came from another serviceId
        if (!serviceId.equals(event.getServiceId())) {
            return;
        }

        final LocalDateTime timestamp = LocalDateTime.now();
        clusterHeartbeatRegistry.put(event.getUuid(), timestamp);
    }

    /**
     * Returns true if the current instance is the leader
     * @return
     */
    public boolean isLeader() {
        purgeExpiredInstances();

        List<String> sortedUuids = new ArrayList<>(clusterHeartbeatRegistry.keySet());

        if (sortedUuids.size() == 0) {
            return false;
        }

        Collections.sort(sortedUuids);
        return myUuid.equals(sortedUuids.get(0));
    }

    @Scheduled(fixedDelay = INSTANCE_HEARTBEAT_INTERVAL)
    public void sendHeartbeat() {
       // logger.info("contextId: " + context.getId());
        context.publishEvent(new ElectionHeartbeatBusEvent(this, context.getId(),myUuid, serviceId));
    }

    void purgeExpiredInstances() {
        clusterHeartbeatRegistry = clusterHeartbeatRegistry.entrySet().stream()
                .filter(map -> map.getValue().compareTo(LocalDateTime.now().minusSeconds(INSTANCE_HEARTBEAT_TIMEOUT/1000)) > 0)
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));
    }

    public static String getMyUuid() {
        return myUuid;
    }

    public static void setMyUuid(String myUuid) {
        LeaderElectionManager.myUuid = myUuid;
    }

    public Map<String, LocalDateTime> getClusterHeartbeatRegistry() {
        return clusterHeartbeatRegistry;
    }

    public LeaderElectionManager setClusterHeartbeatRegistry(Map<String, LocalDateTime> clusterHeartbeatRegistry) {
        this.clusterHeartbeatRegistry = clusterHeartbeatRegistry;
        return this;
    }
}






































