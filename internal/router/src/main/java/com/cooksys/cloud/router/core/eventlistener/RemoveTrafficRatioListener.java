package com.cooksys.cloud.router.core.eventlistener;

import com.cooksys.cloud.router.core.TrafficRatioManager;
import com.cooksys.cloud.commons.event.router.RemoveTrafficRatioBusEvent;
import org.springframework.context.ApplicationListener;

/**
 * Event listener for {@link RemoveTrafficRatioBusEvent}
 *
 * @author Tim Davidson
 */
public class RemoveTrafficRatioListener implements ApplicationListener<RemoveTrafficRatioBusEvent> {

    private TrafficRatioManager trafficRatioManager;

    public RemoveTrafficRatioListener(TrafficRatioManager trafficRatioManager) {
        this.trafficRatioManager = trafficRatioManager;
    }

    @Override
    public void onApplicationEvent(RemoveTrafficRatioBusEvent event) {
        trafficRatioManager.deleteRatio(event.getServiceId());
    }
}
