package com.cooksys.cloud.sdk.timed;

import java.util.HashMap;
import java.util.Map;

public class AggregateStopwatch extends TaskStopwatch {

    private final Map<String, TaskStopwatch> timedTasks = new HashMap<String, TaskStopwatch>();

    public Map<String, TaskStopwatch> getTimedTasks() {
        return timedTasks;
    }

}
