package com.cooksys.cloud.cloudmanager.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaleState;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import com.cooksys.cloud.commons.event.sdk.DecommissionServiceInstanceBusEvent;
import com.cooksys.cloud.commons.util.SpringContext;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.LeaseInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

/**
 * Test class for {@link ServiceInstanceShutdownInitiator}
 *
 * @author Tim Davidson
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceShutdownInitiatorTest {
    @Mock
    private DiscoveryClient discoveryClient;

    @Mock
    private ServiceStateManager serviceStateManager;

    @Mock
    private ApplicationEventPublisher context;

    @InjectMocks
    private ServiceInstanceShutdownInitiator shutdownInitiator;

    @Before
    public void setUp() {
        SpringContext springContext = new SpringContext();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        springContext.setApplicationContext(applicationContext);
        doReturn("context_id").when(applicationContext).getId();

        doReturn(buildApplication()).when(discoveryClient).getApplication("airlines");
    }

    @Test
    public void normalScaledown() throws Exception {
        ArgumentCaptor<Service> serviceCaptor = ArgumentCaptor.forClass(Service.class);
        ArgumentCaptor<ScaleState> scaleStateCaptor = ArgumentCaptor.forClass(ScaleState.class);
        ArgumentCaptor<DecommissionServiceInstanceBusEvent> eventCaptor = ArgumentCaptor.forClass(DecommissionServiceInstanceBusEvent.class);

        doReturn(ScaleState.NORMAL).when(serviceStateManager).getServiceState(any());

        shutdownInitiator.initiateShutdown("airlines","1.0.1");

        verify(serviceStateManager).updateServiceState(serviceCaptor.capture(),scaleStateCaptor.capture());
        verify(context).publishEvent(eventCaptor.capture());

        assertTrue(scaleStateCaptor.getValue().equals(ScaleState.SCALING));
        assertTrue(serviceCaptor.getValue().equals(new Service("airlines","1.0.1")));
        assertTrue(eventCaptor.getValue().getInstanceId().equals("1"));
    }

    @Test
    public void scaleAlreadyInProgress() {
        doReturn(ScaleState.SCALING).when(serviceStateManager).getServiceState(any());

        shutdownInitiator.initiateShutdown("airlines","1.0.1");
        verify(serviceStateManager,never()).updateServiceState(any(),any());
    }

    @Test
    public void coolDownInProgress() {
        doReturn(ScaleState.COOLDOWN).when(serviceStateManager).getServiceState(any());

        shutdownInitiator.initiateShutdown("airlines","1.0.1");
        verify(serviceStateManager,never()).updateServiceState(any(),any());
    }

    private Application buildApplication() {
        final List<InstanceInfo> instances = new ArrayList<>();

        final InstanceInfo instance1 = InstanceInfo.Builder.newBuilder()
                .setMetadata(buildInstanceMetadata("1.0.1"))
                .setLeaseInfo(LeaseInfo.Builder.newBuilder().setServiceUpTimestamp(22222L).build())
                .setInstanceId("1")
                .setAppName("airlines").build();

        final InstanceInfo instance2 = InstanceInfo.Builder.newBuilder()
                .setMetadata(buildInstanceMetadata("1.0.1"))
                .setLeaseInfo(LeaseInfo.Builder.newBuilder().setServiceUpTimestamp(33333L).build())
                .setInstanceId("2")
                .setAppName("airlines").build();

        // throw another version in there to make sure it is filtered from the list
        final InstanceInfo instance3 = InstanceInfo.Builder.newBuilder()
                .setMetadata(buildInstanceMetadata("1.0.2"))
                .setLeaseInfo(LeaseInfo.Builder.newBuilder().setServiceUpTimestamp(11111L).build())
                .setInstanceId("3")
                .setAppName("airlines").build();
        instances.add(instance1);
        instances.add(instance2);
        instances.add(instance3);

        return new Application("airlines",instances);
    }

    Map<String,String> buildInstanceMetadata(String version) {
        Map<String,String> metadata = new HashMap<>();
        metadata.put("version",version);
        return metadata;
    }
}