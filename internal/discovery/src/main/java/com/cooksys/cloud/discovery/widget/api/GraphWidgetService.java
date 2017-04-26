package com.cooksys.cloud.discovery.widget.api;

import java.util.Map;

/**
 * Interface for graph widget service
 *
 * @author Tim Davidson
 */
public interface GraphWidgetService {
    Map<String,GraphRoot> generateDiscoveryGraph();
}
