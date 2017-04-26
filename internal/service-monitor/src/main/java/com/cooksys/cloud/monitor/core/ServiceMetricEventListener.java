package com.cooksys.cloud.monitor.core;

import com.cooksys.cloud.commons.event.cloudmanager.ScaledownServiceBusEvent;
import com.cooksys.cloud.commons.event.cloudmanager.ScaleupServiceBusEvent;
import com.cooksys.cloud.commons.event.cloudmanager.Service;
import com.cooksys.cloud.monitor.configuration.ServiceMonitorProperties;
import com.cooksys.cloud.monitor.event.ServiceMetricEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Event Listener for {@link ServiceMetricEvent}
 * This is the main monitoring for service metrics - the event is currently published in the context by
 * {@link TurbineStreamListenerThread}.  This class contains the logic for publishing scaleup/scaledown events to
 * cloud-manager based on error-percentage metrics gathered from the router edge-proxy.
 *
 * When scale up is triggered, this code will start keeping track of the throughput until the error percentage is under
 * the threshold, then temporarily saves that throughput value and uses it for scaling down.  The scale-down throughput
 * percentage is configurable - see {@link ServiceMonitorProperties}
 *
 * @author Tim Davidson
 */
public class ServiceMetricEventListener implements ApplicationListener<ServiceMetricEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ServiceMetricEventListener.class);
    private Map<Service, Integer> errorThresholdCounters = new HashMap<>();

    private final ApplicationContext context;
    private final ServiceMonitorProperties config;
    private final ServiceThroughputTracker serviceThroughputTracker;

    public ServiceMetricEventListener(ApplicationContext context, ServiceMonitorProperties config, ServiceThroughputTracker serviceThroughputTracker) {
        this.context = context;
        this.config = config;
        this.serviceThroughputTracker = serviceThroughputTracker;
    }

    @Override
    public void onApplicationEvent(ServiceMetricEvent event) {
        logger.debug("ServiceMetricEvent: " + event.toString());
        final Service service = new Service(event.getServiceId(), event.getVersion());

        if (serviceThroughputTracker.isTrackingThroughput(service)) {
            serviceThroughputTracker.setThroughput(service, event.getRequestCount());
        }

        if (event.getErrorPercentage() >= config.getErrorPercentageThreshold()
                && event.getRequestCount() > 100) {
            incrementCounter(service);

            if (errorThresholdCounters.get(service).equals(config.getThresholdBreachedCounterMax())) {
                final ScaleupServiceBusEvent scaleupServiceBusEvent = new ScaleupServiceBusEvent(this, event.getServiceId(), event.getVersion(), 1);

                logger.info("publishing ScaleUpServiceBusEvent: " + scaleupServiceBusEvent.toString());
                context.publishEvent(scaleupServiceBusEvent);
                if (!serviceThroughputTracker.isTrackingThroughput(service)) {
                    serviceThroughputTracker.startTracking(service);
                }
                errorThresholdCounters.remove(service);
            }
        } else {
            decrementCounter(service);
            if (errorThresholdCounters.get(service).equals(0)) {
                logger.debug("error threshold counter at 0 for serviceId: " + service.getServiceId() + " version: " + service.getVersion());
                if (serviceThroughputTracker.containsEntry(service) && serviceThroughputTracker.isTrackingThroughput(service)) {
                    serviceThroughputTracker.stopTracking(service);
                } else {
                    final int currentInstanceCount = serviceThroughputTracker.getInstanceCount(service);

                    long maxThroughputPerInstance = serviceThroughputTracker.getMaxThroughputPerInstance(service);

                    if(currentInstanceCount==0) {
                        return;
                    }

                    long currentThroughputPerInstance = event.getRequestCount() / currentInstanceCount;
                    final int scaledownThroughputPercentage = config.getScaledownThroughputPercentage();

                    logger.debug("currentInstanceCount: " + currentInstanceCount
                            + " maxThroughputPerInstance: " + maxThroughputPerInstance
                            + " currentThroughputPerInstance: " + currentThroughputPerInstance
                    + " scaledownThroughputPercentage: " + scaledownThroughputPercentage);

                    if(maxThroughputPerInstance!=0) {
                        if ((double) currentThroughputPerInstance / (double) maxThroughputPerInstance * 100 < scaledownThroughputPercentage) {
                            // we are under the threshold, scale down
                            logger.info("current throughput is under the threshold - scaling down service: " + service);
                            final ScaledownServiceBusEvent scaledownServiceBusEvent =
                                    new ScaledownServiceBusEvent(this, service.getServiceId(), service.getVersion(), 1);
                            context.publishEvent(scaledownServiceBusEvent);
                        }
                    }
                }
            }
        }

    }

    private void incrementCounter(Service service) {
        if (!errorThresholdCounters.containsKey(service)) {
            errorThresholdCounters.put(service, 1);
        } else {
            if (errorThresholdCounters.get(service) < config.getThresholdBreachedCounterMax()) {
                errorThresholdCounters.put(service, errorThresholdCounters.get(service) + 1);
            }
        }
    }

    private void decrementCounter(Service service) {
        if (!errorThresholdCounters.containsKey(service)) {
            errorThresholdCounters.put(service, 0);
        } else {
            if (errorThresholdCounters.get(service) > 0) {
                errorThresholdCounters.put(service, errorThresholdCounters.get(service) - 1);
            }
        }
    }

}
