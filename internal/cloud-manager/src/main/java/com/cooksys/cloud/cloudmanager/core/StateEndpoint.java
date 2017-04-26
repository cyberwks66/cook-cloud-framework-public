package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Actuator endpoint for inspecting the current service scale state map
 *
 * @author Tim Davidson
 */
public class StateEndpoint extends AbstractEndpoint<Map<String,Object>>{

    private ServiceStateManager serviceStateManager;
    private ServiceClusterStateManager serviceClusterStateManager;

    public StateEndpoint(ServiceStateManager serviceStateManager, ServiceClusterStateManager serviceClusterStateManager) {
        super("state");
        this.serviceStateManager = serviceStateManager;
        this.serviceClusterStateManager = serviceClusterStateManager;
    }

    @Override
    public Map<String, Object> invoke() {
        final Map<String,Object> response = new LinkedHashMap<>();
        response.put("cluster",serviceClusterStateManager.getClusterState());

        final Map<String,ScaleState> serviceStates = serviceStateManager.getServiceStateMapCopy().entrySet().stream()
                .collect(Collectors.toMap(e -> Service.toServiceDescriptor(e.getKey()),e -> e.getValue()));

        response.put("services",serviceStates);
        return response;
    }
}
