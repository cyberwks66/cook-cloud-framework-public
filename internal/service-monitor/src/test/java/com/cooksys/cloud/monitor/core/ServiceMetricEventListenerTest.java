package com.cooksys.cloud.monitor.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaledownServiceBusEvent;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleupServiceBusEvent;
import com.cooksys.cloud.commons.util.SpringContext;
import com.cooksys.cloud.monitor.configuration.ServiceMonitorProperties;
import com.cooksys.cloud.monitor.event.ServiceMetricEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link ServiceMetricEventListener}
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceMetricEventListenerTest {

    public static final String SERVICE_ID = "airlines";
    public static final String VERSION = "1.0.2";

    @Mock
    private ApplicationContext context;

    @Mock
    private ServiceMonitorProperties config;

    @Mock
    private ServiceThroughputTracker serviceThroughputTracker;

    @InjectMocks
    private ServiceMetricEventListener listener;

    @Before
    public void setUp() {
        SpringContext springContext = new SpringContext();
        final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        springContext.setApplicationContext(applicationContext);
        doReturn("contextId").when(applicationContext).getId();
        doReturn(3).when(config).getErrorPercentageThreshold();
        doReturn(2).when(config).getThresholdBreachedCounterMax();
        doReturn(50).when(config).getScaledownThroughputPercentage();
    }

    @Test
    public void testNoScale() {
        doReturn(false).when(serviceThroughputTracker).containsEntry(any());
        final ServiceMetricEvent event = new ServiceMetricEvent(this, SERVICE_ID, VERSION,
                200, 1, 3);

        listener.onApplicationEvent(event);
        verifyNoScaleEventsPublished();

    }

    @Test
    public void testIgnoreEventIfRequestCountIsLow() {
        doReturn(false).when(serviceThroughputTracker).containsEntry(any());
        final ServiceMetricEvent event = new ServiceMetricEvent(this, SERVICE_ID, VERSION,
                90, 80, 3);

        listener.onApplicationEvent(event);

        verifyNoScaleEventsPublished();
    }

    @Test
    public void testIncrementCounterNoScale() {
        final ServiceMetricEvent event = new ServiceMetricEvent(this, SERVICE_ID, VERSION,
                1000, 80, 3);

        listener.onApplicationEvent(event);

        verifyNoScaleEventsPublished();
    }

    @Test
    public void testScaleUp() {
        final ServiceMetricEvent event = new ServiceMetricEvent(this, SERVICE_ID, VERSION,
                1000, 80, 3);

        listener.onApplicationEvent(event);
        listener.onApplicationEvent(event);

        verifyScaleUpEventPublished();
    }

    @Test
    public void testBelowErrorThresholdButNoScaleDown() {
        final ServiceMetricEvent event = new ServiceMetricEvent(this, SERVICE_ID, VERSION,
                75, 0, 3);

        doReturn(true).when(serviceThroughputTracker).containsEntry(any());
        doReturn(true).when(serviceThroughputTracker).isTrackingThroughput(any());
        listener.onApplicationEvent(event);

        doReturn(false).when(serviceThroughputTracker).isTrackingThroughput(any());
        doReturn(true).when(serviceThroughputTracker).containsEntry(any());
        doReturn(3).when(serviceThroughputTracker).getInstanceCount(any());
        doReturn(50L).when(serviceThroughputTracker).getMaxThroughputPerInstance(any());
        listener.onApplicationEvent(event);

        verifyNoScaleEventsPublished();
    }

    @Test
    public void testScaleDown() {
        final ServiceMetricEvent event = new ServiceMetricEvent(this, SERVICE_ID, VERSION,
                74, 0, 3);

        doReturn(true).when(serviceThroughputTracker).containsEntry(any());
        doReturn(true).when(serviceThroughputTracker).isTrackingThroughput(any());
        listener.onApplicationEvent(event);

        doReturn(false).when(serviceThroughputTracker).isTrackingThroughput(any());
        doReturn(true).when(serviceThroughputTracker).containsEntry(any());
        doReturn(3).when(serviceThroughputTracker).getInstanceCount(any());
        doReturn(50L).when(serviceThroughputTracker).getMaxThroughputPerInstance(any());
        listener.onApplicationEvent(event);
        verifyScaleDownEventPublished();
    }

    private void verifyNoScaleEventsPublished() {
        verify(context, times(0)).publishEvent(any());
    }

    private void verifyScaleUpEventPublished() {
        verify(context, times(1)).publishEvent(any(ScaleupServiceBusEvent.class));
    }

    private void verifyScaleDownEventPublished() {
        verify(context, times(1)).publishEvent(any(ScaledownServiceBusEvent.class));
    }
}