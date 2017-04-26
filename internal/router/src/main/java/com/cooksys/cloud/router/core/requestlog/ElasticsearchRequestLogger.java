package com.cooksys.cloud.router.core.requestlog;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link RequestLogger}
 * Logs every request with responsetime metric directly to elasticsearch for use in Kibana dashboards
 *
 * @author Tim Davidson
 */
public class ElasticsearchRequestLogger implements RequestLogger {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchRequestLogger.class);

    private Client elasticsearchClient;
    private ThreadPoolTaskExecutor taskExecuter;

    @Value("${elasticsearchLoggingAppender.indexPrefix}")
    private String indexPrefix;

    public ElasticsearchRequestLogger(Client elasticsearchClient, ThreadPoolTaskExecutor taskExecuter) {
        this.elasticsearchClient = elasticsearchClient;
        this.taskExecuter = taskExecuter;
    }

    @Override
    public void queueLogEntry(RequestLogEntry entry) {
        try {
            taskExecuter.execute(() -> {
                // build the time-based index (new index every hour)
                LocalDateTime dateTime = LocalDateTime.now();

                String timeBasedIndex = new StringBuilder()
                        .append(indexPrefix)
                        .append("-")
                        .append(dateTime.getMonthValue())
                        .append(".")
                        .append(dateTime.getDayOfMonth())
                        .append(".")
                        .append(dateTime.getYear())
                        .append(".")
                        .append(dateTime.getHour())
                        .toString();

                IndexResponse response = null;

                try {
                    response = elasticsearchClient.prepareIndex(timeBasedIndex, "log")
                            .setSource(buildDocument(entry))
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                logger.info(response.toString() + "***LogEntry");
            });
        } catch (TaskRejectedException e) {
            logger.warn("ThreadPoolTaskExecutor rejected request logging task - consider raising the queueCapacity");
        }
    }

    private static Map<String, Object> buildDocument(RequestLogEntry entry) {
        Map<String, Object> document = new HashMap<>();
        document.put("timestamp", entry.getTimestamp());
        document.put("eventName", entry.getEventName());
        document.put("requestHostHeader", entry.getRequestHostHeader());
        document.put("requestPath", entry.getRequestPath());
        document.put("requestMethod", entry.getRequestMethod());
        document.put("serviceId", entry.getServiceId());
        document.put("routeVersion", entry.getRouteVersion());
        document.put("responseCode", entry.getResponseCode());
        document.put("error", entry.isError());
        document.put("errorMessage", entry.getErrorMessage());
        document.put("responseTime", entry.getResponseTime());
        return document;
    }
}
