package com.cooksys.cloud.router.core.eventlistener;

import com.cooksys.cloud.router.core.RouteManager;
import com.cooksys.cloud.commons.event.router.RemoveRouteBusEvent;
import org.springframework.context.ApplicationListener;

/**
 * Event listener for {@link RemoveRouteBusEvent}
 *
 * @author Tim Davidson
 */
public class RemoveRouteListener implements ApplicationListener<RemoveRouteBusEvent> {
    private RouteManager routeManager;

    public RemoveRouteListener(RouteManager routeManager) {
        this.routeManager = routeManager;
    }

    @Override
    public void onApplicationEvent(RemoveRouteBusEvent event) {

    }
}
