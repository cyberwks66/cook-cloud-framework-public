package com.cooksys.cloud.sdk.timed;

import org.springframework.util.StopWatch;

import java.util.Date;

public class TaskStopwatch extends StopWatch {
    private TaskStatus status = TaskStatus.NOT_STARTED;

    private Date startTime;
    private Date endTime;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public void start() throws IllegalStateException {
        startTime = new Date();
        super.start();
        status = TaskStatus.RUNNING;
    }

    @Override
    public void stop() throws IllegalStateException {
        endTime = new Date();
        super.stop();
        status = TaskStatus.COMPLETE;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
