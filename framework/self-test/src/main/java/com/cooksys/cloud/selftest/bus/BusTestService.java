package com.cooksys.cloud.selftest.bus;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.beans.BeansException;
import org.springframework.cloud.bus.event.selftest.BusTestAckRemoteApplicationEvent;
import org.springframework.cloud.bus.event.selftest.BusTestRemoteApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

/**
 * Service to facilitate testing the spring cloud bus.
 */
public class BusTestService implements ApplicationContextAware {

    private static final int MAX_STORED_BUS_TESTS = 10;

    private ApplicationContext context;

    // ApplicationEventPublisher bean is provided by spring so we can publish events to the ApplicationContext
    private ApplicationEventPublisher eventPublisher;

    // Create a queue with a size limit, so we don't have to worry about this list growing large over time
    private Queue<RemoteEventTestSequence> eventTests = new CircularFifoQueue<>(MAX_STORED_BUS_TESTS);


    private String source;

    public BusTestService(ApplicationEventPublisher eventPublisher, String source) {
        this.eventPublisher = eventPublisher;
        this.source = source;
    }

    /**
     * Creates a new test - publishes a BusTestRemoteApplicationEvent that holds a unique identifier.
     * All the instances will acknowledge with a BusTestAckRemoteApplicationEvent
     *
     * @return unique identifier for referencing the test later (uuid)
     */
    public synchronized String initiateTestSequence() {
        final BusTestRemoteApplicationEvent event = new BusTestRemoteApplicationEvent(this, this.context.getId(), UUID.randomUUID().toString());

        eventPublisher.publishEvent(event);
        return event.getUuid();
    }

    /**
     * Send an acknowledge event containing uuid from BusTestRemoteApplicationEvent and instanceId of sender
     *
     * @param uuid
     * @param instanceId
     */
    public void sendAcknowledgeEvent(String uuid, String instanceId) {
        final BusTestAckRemoteApplicationEvent event = new BusTestAckRemoteApplicationEvent(this, this.context.getId(), uuid, instanceId);
        eventPublisher.publishEvent(event);
    }

    /**
     * Method called when an ACK event is received - stores the instanceId of the event publisher
     *
     * @param uuid       - unique identifier of the test
     * @param instanceId - instanceId of the event publisher
     */
    public synchronized void storeBusTestAck(String uuid, String instanceId) throws RuntimeException {
        RemoteEventTestSequence testSequence = getTestSequence(uuid);

        if (testSequence == null) {
            testSequence = new RemoteEventTestSequence(uuid, new Date());
            eventTests.add(testSequence);
        }

        testSequence.getAcknowledgedInstances().add(instanceId);
    }


    /**
     * Returns the test results for a given test uuid
     *
     * @param uuid
     * @return test details containing instanceIds that acknowledged the initial event
     */
    public Optional<RemoteEventTestSequence> getTestResults(String uuid) {
        final RemoteEventTestSequence testResults = getTestSequence(uuid);

        if (testResults == null) {
            return Optional.empty();
        }

        return Optional.of(testResults);
    }

    private RemoteEventTestSequence getTestSequence(String uuid) {
        RemoteEventTestSequence testSequence = null;

        for (RemoteEventTestSequence sequence : eventTests) {
            if (uuid.equals(sequence.getTestId())) {
                testSequence = sequence;
                break;
            }
        }
        return testSequence;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
