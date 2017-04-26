package com.cooksys.cloud.router.actuator;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

/**
 * Defines the /routes actuator endpoint
 *
 * @author Tim Davidson
 */
public class RoutesEndpoint extends AbstractEndpoint<String> {

    public static final String ROUTES = "routes";

    public RoutesEndpoint() {
        super(ROUTES);
    }

    @Override
    public String invoke() {
        return null;
    }
}
