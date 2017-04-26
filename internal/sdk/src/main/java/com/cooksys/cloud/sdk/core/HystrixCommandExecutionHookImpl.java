package com.cooksys.cloud.sdk.core;

import ch.qos.logback.classic.Logger;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.exception.HystrixRuntimeException.FailureType;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import org.slf4j.LoggerFactory;

/**
 * Execution hook that will handle logging for certain Hystrix execution points.
 *
 * @author Tim Davidson
 */
public class HystrixCommandExecutionHookImpl extends HystrixCommandExecutionHook {

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Override
    public <T> Exception onExecutionError(HystrixInvokable<T> commandInstance, Exception e) {
        logger.error("Hystrix command execution error");

        return super.onExecutionError(commandInstance, e);
    }

    /**
     * Invoked when {@link HystrixInvokable} fails with an Exception.
     *
     * @param commandInstance The executing HystrixInvokable instance.
     * @param failureType     {@link FailureType} enum representing which type of error
     * @param e               exception object
     * @since 1.2
     */
    @Override
    public <T> Exception onError(HystrixInvokable<T> commandInstance, FailureType failureType, Exception e) {
        logger.error("Hystrix command error:  - FailureType: " + failureType.toString());
        return e; // by default, just pass through
    }

    /**
     * Invoked when the fallback method in {@link HystrixInvokable} starts.
     *
     * @param commandInstance The executing HystrixInvokable instance.
     * @since 1.2
     */
    @Override
    public <T> void onFallbackStart(HystrixInvokable<T> commandInstance) {
        logger.info("Hystrix command failback started");
        // do nothing by default
    }

    /**
     * Invoked when the fallback method in {@link HystrixInvokable} fails with
     * an Exception.
     *
     * @param commandInstance The executing HystrixInvokable instance.
     * @param e               exception object
     * @since 1.2
     */
    @Override
    public <T> Exception onFallbackError(HystrixInvokable<T> commandInstance, Exception e) {
        logger.error("Hystrix command fallback error");
        return e;
    }

}
