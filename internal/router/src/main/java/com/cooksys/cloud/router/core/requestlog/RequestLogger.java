package com.cooksys.cloud.router.core.requestlog;

/**
 * Interface for request logging imlementations
 *
 * @author Tim Davidson
 */
public interface RequestLogger {
    void queueLogEntry(RequestLogEntry entry);
}
