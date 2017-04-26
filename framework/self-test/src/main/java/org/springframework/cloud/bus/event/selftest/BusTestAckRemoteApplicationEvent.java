package org.springframework.cloud.bus.event.selftest;

import com.google.common.base.MoreObjects;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * Created by timd on 2/21/17.
 */
public class BusTestAckRemoteApplicationEvent extends RemoteApplicationEvent {
    private String uuid;
    private String instanceId;

    public BusTestAckRemoteApplicationEvent() {
        // for serializers
    }

    public BusTestAckRemoteApplicationEvent(Object source, String originService, String uuid, String instanceId) {
        super(source,originService);
        this.uuid=uuid;
        this.instanceId=instanceId;
    }

    public String getUuid() {
        return uuid;
    }

    public BusTestAckRemoteApplicationEvent setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public BusTestAckRemoteApplicationEvent setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("instanceId", instanceId)
                .toString();
    }
}
