package com.cooksys.cloud.discovery.widget.api;

/**
 * Node type enum for graph widget API
 *
 * @author Tim Davidson
 */
public enum NodeType {
    CORE_COMPONENT("core_component"),
    MICROSERVICE("microservice"),
    HOST("host");

    private String value;

    NodeType(String type) {
        this.value =type;
    }

    public String getValue() {
        return value;
    }
}
