package com.cooksys.cloud.commons.leaderelection;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Actuator endpoint for inspecting leader election details
 *
 * @author Tim Davidson
 */
public class ElectionEndpoint extends AbstractEndpoint<Map<String,Object>> {
    private LeaderElectionManager electionManager;

    public ElectionEndpoint(LeaderElectionManager electionManager) {
        super("election");
        this.electionManager = electionManager;
    }

    @Override
    public Map<String, Object> invoke() {
       final Map<String,LocalDateTime> sortedHeartbeatRegistry = new TreeMap<>(electionManager.getClusterHeartbeatRegistry());

        final Map<String,Object> response = new HashMap<>();
        response.put("myUuid",electionManager.myUuid);
        response.put("isLeader",electionManager.isLeader());

        final Map<String,String> formattedRegistry = sortedHeartbeatRegistry.entrySet().stream()
               .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toString()));

        response.put("clusterHeartbeatRegistry",formattedRegistry);

        return response;
    }
}
