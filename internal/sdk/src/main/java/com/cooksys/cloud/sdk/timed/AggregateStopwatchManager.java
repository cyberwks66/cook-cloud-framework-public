package com.cooksys.cloud.sdk.timed;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Component
@Scope("request")
public class AggregateStopwatchManager {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    private final Map<String, AggregateStopwatch> aggregatedTasks = new HashMap<String, AggregateStopwatch>();

    @Autowired
    private TimedTaskAnnotationProcessor processor;

    @PostConstruct
    public void init() {
        // build the StopWatch entries from the index created by the annotation
        // processor
        for (final Entry<String, Set<String>> aggregateEntry : processor.getTaskIndex().entrySet()) {
            final AggregateStopwatch stopwatchEntry = new AggregateStopwatch();

            aggregatedTasks.put(aggregateEntry.getKey(), stopwatchEntry);

            for (final String nameEntry : aggregateEntry.getValue()) {
                logger.info("adding stopwatchEntry - aggregate: " + aggregateEntry.getKey() + " task: " + nameEntry);
                stopwatchEntry.getTimedTasks().put(nameEntry, new TaskStopwatch());
            }
        }
    }

    public synchronized void start(String aggregatedTaskName, String taskName) throws IllegalStateException {
        final AggregateStopwatch aggregate = aggregatedTasks.get(aggregatedTaskName);

        if (aggregate == null) {
            throw new IllegalStateException(
                    "Start task failed: Aggregated task name '" + aggregatedTaskName + "' not found in task index");
        }

        if (aggregate.getTimedTasks().get(taskName) == null) {
            throw new IllegalStateException("Start task failed: Task name '" + taskName
                    + "' not found in task index - aggregate name: " + aggregatedTaskName);
        }

        if (!"[none]".equals(aggregatedTaskName) && aggregate.getStatus() != TaskStatus.RUNNING) {
            aggregate.start();
        }

        aggregate.getTimedTasks().get(taskName).start();
    }

    public synchronized void stop(String aggregatedTaskName, String taskName) throws IllegalStateException {
        final AggregateStopwatch aggregate = aggregatedTasks.get(aggregatedTaskName);

        if (aggregate == null) {
            throw new IllegalStateException(
                    "Stop task failed: Aggregated task name '" + aggregatedTaskName + "' not found in task index");
        }

        if (aggregate.getTimedTasks().get(taskName) == null) {
            throw new IllegalStateException("Stop task failed: Task name '" + taskName
                    + "' not found in task index - aggregate name: " + aggregatedTaskName);
        }

        aggregate.getTimedTasks().get(taskName).stop();

        if (!"[none]".equals(aggregatedTaskName) && allTasksComplete(aggregatedTaskName)) {
            aggregate.stop();
        }
    }

    public synchronized long getTotalExecutionTime(String aggregatedTaskName) throws IllegalStateException {
        final AggregateStopwatch aggregate = aggregatedTasks.get(aggregatedTaskName);

        if (aggregate.getStatus() != TaskStatus.COMPLETE) {
            throw new IllegalStateException("Aggregated task '" + aggregatedTaskName + "' has not completed");
        }

        long totalTime = 0;

        for (final TaskStopwatch task : aggregate.getTimedTasks().values()) {
            totalTime += task.getTotalTimeMillis();
        }

        return totalTime;
    }

    public synchronized long getExecutionTime(String aggregatedTaskName, String taskName) throws IllegalStateException {

        final TaskStopwatch task = aggregatedTasks.get(aggregatedTaskName).getTimedTasks().get(taskName);

        if (task.getStatus() != TaskStatus.COMPLETE) {
            throw new IllegalStateException(
                    "Task '" + taskName + "' in Aggregated task '" + aggregatedTaskName + "' has not completed");
        }

        return task.getTotalTimeMillis();
    }

    public synchronized Date getStartTime(String aggregatedTaskName) {
        return aggregatedTasks.get(aggregatedTaskName).getStartTime();
    }

    public synchronized Date getStartTime(String aggregatedTaskName, String taskName) {
        logger.info("getExecutionTime() aggregate: " + aggregatedTaskName + " task: " + taskName);
        return aggregatedTasks.get(aggregatedTaskName).getTimedTasks().get(taskName).getStartTime();
    }

    public synchronized Date getEndTime(String aggregatedTaskName) {
        return aggregatedTasks.get(aggregatedTaskName).getEndTime();
    }

    public synchronized Date getEndTime(String aggregatedTaskName, String taskName) {
        return aggregatedTasks.get(aggregatedTaskName).getTimedTasks().get(taskName).getEndTime();
    }

    public synchronized boolean isComplete(String aggregatedTaskName) {
        return aggregatedTasks.get(aggregatedTaskName).getStatus() == TaskStatus.COMPLETE;
    }

    private boolean allTasksComplete(String aggregatedTaskName) {
        final AggregateStopwatch aggregate = aggregatedTasks.get(aggregatedTaskName);

        for (final TaskStopwatch task : aggregate.getTimedTasks().values()) {
            if (task.getStatus() == TaskStatus.NOT_STARTED || task.getStatus() == TaskStatus.RUNNING) {
                return false;
            }
        }

        return true;
    }
}
