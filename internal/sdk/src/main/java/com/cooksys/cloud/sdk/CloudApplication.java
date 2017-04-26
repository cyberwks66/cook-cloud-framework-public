package com.cooksys.cloud.sdk;

import com.cooksys.cloud.commons.SharedConstants;
import com.cooksys.cloud.sdk.core.HystrixCommandExecutionHookImpl;
import com.cooksys.cloud.sdk.util.AwsUtil;
import com.github.zafarkhaja.semver.Version;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Main entry point for an API running on the Cloud Framework.
 *
 * @author Tim Davidson
 */

public class CloudApplication {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CloudApplication.class);
    private static final String EUREKA_INSTANCE_INSTANCE_ID = "--eureka.instance.instance-id";
    private static final String SPRING_APPLICATION_INDEX = "--spring.application.index";
    public static final String EUREKA_INSTANCE_METADATA_MAP = "--eureka.instance.metadataMap.";
    private static final String EQUALS = "=";
    public static final String EUREKA_INSTANCE_METADATA_MAP_PROXY_HOST = EUREKA_INSTANCE_METADATA_MAP + SharedConstants.EUREKA_METAKEY_PROXY_HOST + EQUALS;
    public static final String EUREKA_INSTANCE_METADATA_MAP_ECS_TASK_ARN = EUREKA_INSTANCE_METADATA_MAP + SharedConstants.EUREKA_METAKEY_ECS_TASK_ARN + EQUALS;
    public static final String EUREKA_INSTANCE_METADATA_MAP_DOCKER_CONTAINER_ID = EUREKA_INSTANCE_METADATA_MAP + SharedConstants.EUREKA_METAKEY_DOCKER_CONTAINER_ID + EQUALS;
    public static final String EUREKA_INSTANCE_METADATA_MAP_VERSION = EUREKA_INSTANCE_METADATA_MAP + SharedConstants.EUREKA_METAKEY_VERSION + EQUALS;


    /**
     * Launches Spring-boot IOC container with an embedded tomcat instance, pulls configuration from
     * service-manager, and registers with discovery service
     *
     * @param source
     * @param args
     * @return
     */
    public static ConfigurableApplicationContext run(Object source, Version version, String... args) {
        Assert.notNull(source, "source argument can not be null");
        Assert.notNull(version, "version argument can not be null");

        final String command = System.getProperty("sun.java.command");
        final String pathStr = command.split(" ")[0];
        final Path path = Paths.get(pathStr);

        if (path == null || path.getFileName() == null) {
            throw new IllegalStateException("Propery sun.java.command is not set.");
        }

        System.setProperty("logFileName", path.getFileName().toString());
        // MDC.put("logFileName", path.getFileName().toString());

        final Logger logger = Logger.getLogger(String.valueOf(CloudApplication.class));

        logger.info("Starting Cloud Microservice...");

        registerHystrixHook();

        try {
            args = addSpringProperties(args, version.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return SpringApplication.run(new Object[]{source}, args);
    }

    /**
     * register our custom Hystrix command execution hook. Needs to be done at
     * application startup (before any hystrix initialization occurs)
     */
    private static void registerHystrixHook() {
        HystrixPlugins.getInstance().registerCommandExecutionHook(new HystrixCommandExecutionHookImpl());
    }

    /**
     * Adds eureka properties to the argument list
     *
     * @param args array of command-line arguments passed in to main method
     * @return modified arguments array containing eureka properties
     */
    private static String[] addSpringProperties(String[] args, String version) throws IOException {
        final List<String> newArgs = new ArrayList<String>(Arrays.asList(args));

        // Sets the instanceId to <$PID>@<$HOSTNAME>
        final String instanceId = UUID.randomUUID().toString();
        newArgs.add(EUREKA_INSTANCE_INSTANCE_ID + "=" + instanceId);

        // Spring cloud bus requires a unique identifier in the spring application index - we can just use the instanceId here
        newArgs.add(SPRING_APPLICATION_INDEX + "=" + instanceId);

        // Add version to the metadata map - this is used by router, service-monitor, and cloud-manager
        newArgs.add(EUREKA_INSTANCE_METADATA_MAP_VERSION + version);

        // If -aws flag is supplied, get the docker host from AWS metadata and place it in the discovery instance metadata
        boolean aws = false;

        for (String arg : args) {
            if (arg.equals("-aws")) {
                aws = true;
                break;
            }
        }

        if (aws) {
            newArgs.add(EUREKA_INSTANCE_METADATA_MAP_PROXY_HOST + AwsUtil.getEc2HostIpAddress());

            final String dockerContainerId = AwsUtil.getDockerContainerId();
            logger.debug("dockerContainerId: " + dockerContainerId);
            newArgs.add(EUREKA_INSTANCE_METADATA_MAP_DOCKER_CONTAINER_ID + dockerContainerId);

            final String ecsArn = AwsUtil.getTaskArnForContainer(dockerContainerId);
            logger.debug("ecsArn: " + ecsArn);
            newArgs.add(EUREKA_INSTANCE_METADATA_MAP_ECS_TASK_ARN + ecsArn);
        }

        return newArgs.toArray(new String[newArgs.size()]);
    }
}
