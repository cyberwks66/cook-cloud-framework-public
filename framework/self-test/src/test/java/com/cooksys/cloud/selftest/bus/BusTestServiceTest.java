package com.cooksys.cloud.selftest.bus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.cloud.bus.event.selftest.BusTestAckRemoteApplicationEvent;
import org.springframework.cloud.bus.event.selftest.BusTestRemoteApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

/**
 * Created by timd on 2/21/17.
 */
public class BusTestServiceTest {

    private ApplicationEventPublisher eventPublisherMock;
    private ApplicationContext contextMock;

    private BusTestService service;
    private static final String instanceId = "testInstance";
    private static final String source = "self-test";

    @Before
    public void setup() {
        eventPublisherMock = Mockito.mock(ApplicationEventPublisher.class);
        service = new BusTestService(eventPublisherMock,source);
        contextMock = Mockito.mock(ApplicationContext.class);
        service.setApplicationContext(contextMock);
        Mockito.doReturn(source).when(contextMock).getId();
    }
    @Test
    public void testInitiateTestSequence() throws Exception {
        ArgumentCaptor<BusTestRemoteApplicationEvent> argument = ArgumentCaptor.forClass(BusTestRemoteApplicationEvent.class);

        String uuid = service.initiateTestSequence();
        Mockito.verify(eventPublisherMock).publishEvent(argument.capture());
        Assert.assertTrue(argument.getValue().getUuid().equals(uuid));
        Assert.assertTrue(argument.getValue().getOriginService().equals(source));

        Mockito.reset(eventPublisherMock);
    }

    @Test
    public void testSendAcknowledgeEvent() throws Exception {
        ArgumentCaptor<BusTestAckRemoteApplicationEvent> argument = ArgumentCaptor.forClass(BusTestAckRemoteApplicationEvent.class);
        final String uuid = "uuid";

        service.sendAcknowledgeEvent(uuid,instanceId);

        Mockito.verify(eventPublisherMock).publishEvent(argument.capture());
        Assert.assertTrue(argument.getValue().getUuid().equals(uuid));
        Assert.assertTrue(argument.getValue().getInstanceId().equals(instanceId));
        Assert.assertTrue(argument.getValue().getOriginService().equals(source));

        Mockito.reset(eventPublisherMock);
    }

    @Test
    public void testStoreBusTestAckAndTestResults() throws Exception {

        final String instanceId1="instanceId1";
        final String instanceId2="instanceId2";
        final String instanceId3="instanceId3";

        String uuid = service.initiateTestSequence();
        service.storeBusTestAck(uuid,instanceId1);
        service.storeBusTestAck(uuid,instanceId2);
        service.storeBusTestAck(uuid,instanceId3);

        final Optional<RemoteEventTestSequence> results = service.getTestResults(uuid);
        Assert.assertTrue(results.isPresent());
        Assert.assertTrue(results.get().getTestId().equals(uuid));
        Assert.assertTrue(results.get().getAcknowledgedInstances().contains(instanceId1));
        Assert.assertTrue(results.get().getAcknowledgedInstances().contains(instanceId2));
        Assert.assertTrue(results.get().getAcknowledgedInstances().contains(instanceId3));
    }

    @Test
    public void verifyOnlyTenTestsStored() {
        service=new BusTestService(eventPublisherMock,source);
        service.setApplicationContext(contextMock);

        String firstUuid = service.initiateTestSequence();

        for(int i=0;i<10;i++) {
            service.initiateTestSequence();
        }

        Assert.assertFalse(service.getTestResults(firstUuid).isPresent());
    }

}