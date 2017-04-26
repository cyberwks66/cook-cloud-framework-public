package com.cooksys.cloud.commons.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for discovery service that is used by the verify-self-test CLI
 * for checking UUIDs of running self-test instance registered with discovery
 *
 * @author timd
 */
public class SelfTestUuidResponse {
    private List<String> uuids = new ArrayList<String>();

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

}
