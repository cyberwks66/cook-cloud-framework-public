package org.springframework.cloud.bus.event.selftest;

import com.google.common.base.MoreObjects;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * Initial Event generated by self test - consumed by all self-test instances
 */
public class BusTestRemoteApplicationEvent extends RemoteApplicationEvent {
    private String uuid;

    public BusTestRemoteApplicationEvent() {
        // for serializers
    }

    public BusTestRemoteApplicationEvent(Object source, String originService, String uuid) {
        super(source,originService);
        this.uuid=uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public BusTestRemoteApplicationEvent setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .toString();
    }
}