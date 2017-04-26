package com.cooksys.cloud.commons.event;

import com.cooksys.cloud.commons.util.SpringContext;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * Base class for Events that should be sent to the event bus.
 *
 * @author Tim Davidson
 */
public class BusEvent extends RemoteApplicationEvent {
    public BusEvent() {

    }

    public BusEvent(Object source) {
        super(source, SpringContext.getApplicationContext().getId());
    }

}
