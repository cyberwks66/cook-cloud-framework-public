package com.cooksys.cloud.commons.event.election;

import com.cooksys.cloud.commons.leaderelection.EnableLeaderElection;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * Event that is published periodically by services using {@link EnableLeaderElection}.  This is essentially the "vote".
 * Each service instance creates it's own uuid that is used to determine the leader
 *
 * @author Tim Davidson
 */
public class ElectionHeartbeatBusEvent extends RemoteApplicationEvent {
    private String serviceId;
    private String uuid;

    public ElectionHeartbeatBusEvent() {
    }

    public ElectionHeartbeatBusEvent(Object source, String originService, String uuid, String serviceId) {
        super(source, originService, serviceId + ":**");
        this.uuid = uuid;
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public ElectionHeartbeatBusEvent setServiceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public ElectionHeartbeatBusEvent setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
