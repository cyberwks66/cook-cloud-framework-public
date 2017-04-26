package com.cooksys.cloud.commons;

/**
 * Framework and SDK shared constants
 *
 * @author Tim Davidson
 */
public final class SharedConstants {


    /* Logger property names */

    public static final String CONTEXT_ID = "contextId";
    public static final String REQUEST_BODY = "requestBody";

    public static final String EVENT_START = "eventStart";


    /* RequestContext keys */
    public static final String REQUEST_URI = "requestUri";


    /* Discovery client */
    public static final String DISCOVERY_CLIENT_ZONE = "defaultZone";
    public static final int DISCOVERY_SERVER_URL_MAX_COUNT = 3;

    /* Discovery metadata */
    public static final String EUREKA_META_KEY_INSTANCE_ID = "instanceId";
    public static final String EUREKA_META_KEY_PID = "PID";

    /* Configuration command line args */
    public static final String EUREKA_INSTANCE_METADATA_MAP_INSTANCE_ID = "--eureka.instance.metadataMap."
            + EUREKA_META_KEY_INSTANCE_ID + "=";
    public static final String EUREKA_INSTANCE_METADATA_MAP_PID = "--eureka.instance.metadataMap." + EUREKA_META_KEY_PID
            + "=";


    /* HTTP Headers */
    public static final String CLOUD_HDR_CONTEXT_ID = "cloud-context-id";
    public static final String EUREKA_METAKEY_PROXY_HOST = "proxyHost";
    public static final String EUREKA_METAKEY_ECS_TASK_ARN = "ecsTaskArn";
    public static final String EUREKA_METAKEY_DOCKER_CONTAINER_ID = "dockerContainerId";
    public static final String EUREKA_METAKEY_VERSION = "version";

    private SharedConstants() {
        // restrict instantiation
    }

}
