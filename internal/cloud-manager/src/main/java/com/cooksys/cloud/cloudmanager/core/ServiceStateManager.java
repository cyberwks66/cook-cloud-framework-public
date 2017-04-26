package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import com.cooksys.cloud.commons.event.cloudmanager.ServiceStateBusEvent;
import com.cooksys.cloud.commons.leaderelection.LeaderElectionManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager that stores service state information, and handles replication between other instances of cloud-manager
 *
 * @author Tim Davidson
 */
public class ServiceStateManager implements ApplicationListener<ServiceStateBusEvent> {

    private LeaderElectionManager electionManager;
    private ApplicationEventPublisher context;
    private Map<Service,ScaleState> serviceStateMap = new HashMap<>();

    public ServiceStateManager(LeaderElectionManager electionManager, ApplicationEventPublisher context) {
        this.electionManager = electionManager;
        this.context = context;
    }

    public void updateServiceState(Service service, ScaleState scaleState) {
        serviceStateMap.put(service, scaleState);

        final Map<String,ScaleState> serviceDescriptorMap = serviceStateMap.entrySet().stream()
                .collect(Collectors.toMap(e -> Service.toServiceDescriptor(e.getKey()),e -> e.getValue()));
        context.publishEvent(new ServiceStateBusEvent(this,serviceDescriptorMap));
    }

    public ScaleState getServiceState(Service service) {
        if(serviceStateMap.get(service) == null) {
            serviceStateMap.put(service,ScaleState.NORMAL);
        }
        return serviceStateMap.get(service);
    }

    public Map<Service,ScaleState> getServiceStateMapCopy() {
        return new HashMap<>(serviceStateMap);
    }

    @Override
    public void onApplicationEvent(ServiceStateBusEvent event) {
        if(!electionManager.isLeader()) {
            this.serviceStateMap = event.getServiceStateMap().entrySet().stream()
                .collect(Collectors.toMap(e -> Service.fromServiceDescriptor(e.getKey()), e -> e.getValue()));
        }
    }
}
