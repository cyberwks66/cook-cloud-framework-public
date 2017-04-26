package com.cooksys.cloud.sdk.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Utility class for accessing the ECS agent and docker info when deployed to AWS - this metadata is used
 * when registering with eureka to provied the framework with aws specifics on a service running on the SDK
 *
 * @author Tim Davidson
 */
public class AwsUtil {
    public static final String AWS_METADATA_DOCKER_HOST = "http://169.254.169.254/latest/meta-data/local-ipv4";
    private static final Logger logger = LoggerFactory.getLogger(AwsUtil.class);
    public static final String ECS_AGENT_TASKS_URL = "http://172.17.0.1:51678/v1/tasks";
    public static final String UNDEFINED_AGENT_RESPONSE = "UNDEFINED_agent_response";
    public static final String UNDEFINED_AGENT_TASKS = "UNDEFINED_agent_tasks";
    public static final String UNDEFINED_AGENT_CONTAINERS = "UNDEFINED_agent_containers";
    public static final String UNDEFINED_AGENT_TASK_ARN = "UNDEFINED_agent_taskARN";
    public static final String UNDEFINED_AGENT_NO_TASK_MATCH = "UNDEFINED_agent_noTaskMatch";
    public static final String TASKS = "Tasks";
    public static final String CONTAINERS = "Containers";
    public static final String DOCKER_ID = "DockerId";
    public static final String ARN = "Arn";
    public static final String DOCKER = "docker/";
    public static final int DOCKER_OFFSET = 7;

    /**
     * Retrieves the docker host IP address from EC2 Metadata
     *
     * @return
     */
    public static String getEc2HostIpAddress() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(AWS_METADATA_DOCKER_HOST, String.class);
    }

    public static String getDockerContainerId() throws IOException {


        final FileReader fileReader = new FileReader("/proc/self/cgroup");
        final BufferedReader bufferedReader = new BufferedReader(fileReader);

        final String firstLine = bufferedReader.readLine();

        if (firstLine != null && firstLine.contains(DOCKER)) {
            return parseContainerIdFromCgroup(firstLine);
        }
        return null;
    }

    public static String parseContainerIdFromCgroup(String cgroup) {
        // example of expected line in this file (last token is long container ID):
        // 11:cpuset:/docker/7fa7cff26ba95b6830bd4a390db24b21ebf2dba911e09d1609a67d6177526950

        return cgroup.substring(cgroup.indexOf(DOCKER) + DOCKER_OFFSET);
    }


    public static String getTaskArnForContainer(String dockerContainerId) {
        final RestTemplate restTemplate = new RestTemplate();

        String agentResponseJson = null;

        try {
            agentResponseJson = restTemplate.getForObject(ECS_AGENT_TASKS_URL, String.class);
        } catch (RestClientException e) {
            logger.error("Error while contacting ECS agent from within container", e);
        }

        if (agentResponseJson == null || agentResponseJson.isEmpty()) {
            return UNDEFINED_AGENT_RESPONSE;
        }

        final Gson gson = new Gson();
        final Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        final Map<String, Object> agentResponse = gson.fromJson(agentResponseJson, type);

        final List<Map> tasks = (List<Map>) agentResponse.get(TASKS);

        if (tasks == null || tasks.isEmpty()) {
            return UNDEFINED_AGENT_TASKS;
        }

        for (Map task : tasks) {
            final List containers = (List) task.get(CONTAINERS);

            if (containers == null || containers.isEmpty()) {
                return UNDEFINED_AGENT_CONTAINERS;
            }

            final Map container = (Map)containers.get(0);

            if (dockerContainerId.equals((String) container.get(DOCKER_ID))) {
                final String arn = (String) task.get(ARN);

                if(arn==null || arn.isEmpty()) {
                    return UNDEFINED_AGENT_TASK_ARN;
                }
                return (String) task.get(ARN);
            }
        }
        return UNDEFINED_AGENT_NO_TASK_MATCH;
    }
}
