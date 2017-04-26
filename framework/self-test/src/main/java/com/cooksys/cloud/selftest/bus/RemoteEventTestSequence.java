package com.cooksys.cloud.selftest.bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by timd on 2/21/17.
 */
public class RemoteEventTestSequence {
    private String testId;
    private Date testInitiatedTimestamp;
    private List<String> acknowledgedInstances = Collections.synchronizedList(new ArrayList<String>());

    public RemoteEventTestSequence(String testId, Date testInitiatedTimestamp) {
        this.testId = testId;
        this.testInitiatedTimestamp = testInitiatedTimestamp;
    }

    public Date getTestInitiatedTimestamp() {
        return testInitiatedTimestamp;
    }

    public RemoteEventTestSequence setTestInitiatedTimestamp(Date testInitiatedTimestamp) {
        this.testInitiatedTimestamp = testInitiatedTimestamp;
        return this;
    }

    public List<String> getAcknowledgedInstances() {
        return acknowledgedInstances;
    }

    public RemoteEventTestSequence setAcknowledgedInstances(List<String> acknowledgedInstances) {
        this.acknowledgedInstances = acknowledgedInstances;
        return this;
    }

    public String getTestId() {
        return testId;
    }

    public RemoteEventTestSequence setTestId(String testId) {
        this.testId = testId;
        return this;
    }
}
