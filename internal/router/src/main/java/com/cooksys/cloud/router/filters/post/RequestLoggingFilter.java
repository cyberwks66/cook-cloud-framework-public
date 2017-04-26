package com.cooksys.cloud.router.filters.post;

import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.core.requestlog.RequestLogEntry;
import com.cooksys.cloud.router.core.requestlog.RequestLogger;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.github.zafarkhaja.semver.Version;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Zuul filter that logs every request (default implementation is Elasticsearch)
 *
 * @author Tim Davidson
 */
public class RequestLoggingFilter extends ZuulFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    public static final String DELIM = " ";
    public static final String SERVICE_ID = "serviceId";
    public static final String REQUEST_HOST_HEADER = "requestHostHeader";
    public static final String REQUEST_PATH = "requestPath";
    public static final String REQUEST_METHOD = "requestMethod";
    public static final String ROUTE_VERSION = "routeVersion";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String ERROR = "error";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String RESPONSE_TIME = "responseTime";
    public static final String HOST_HEADER = "Host";
    public static final String FALSE = "false";
    public static final String TRUE = "true";
    public static final String EVENT_NAME = "eventName";
    public static final String ROUTER_EDGE_REQUEST = "routerEdgeRequest";

    private RequestContextFactory contextFactory;
    private RequestLogger requestLogger;

    public RequestLoggingFilter(RequestContextFactory contextFactory, RequestLogger requestLogger) {
        this.contextFactory = contextFactory;
        this.requestLogger = requestLogger;
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.REQUEST_LOGGING_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.REQUEST_LOGGING_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        logger.info("run()");
        try {
            final RequestContext context = contextFactory.getZuulRequestContext();
            final HttpServletRequest request = context.getRequest();
            final HttpServletResponse response = context.getResponse();

            if (request == null) {
                logger.error("Request is not set in zuul RequestContext");
                return null;
            }
            if (response == null) {
                logger.error("Response is not set in zuul RequestContext");
                return null;
            }

            final RequestLogEntry logEntry = new RequestLogEntry();

            logEntry.setTimestamp(new Date());
            logEntry.setRequestHostHeader(notNull(request.getHeader(HOST_HEADER), ""));
            logEntry.setRequestPath(notNull(request.getServletPath(), ""));
            logEntry.setRequestMethod(notNull(request.getMethod(), ""));
            logEntry.setServiceId(notNull((String) context.get(ContextConstants.ROUTE_SERVICE_ID), ""));

            Version contextVersion;
            logEntry.setRouteVersion("");
            if (context.get(ContextConstants.ROUTE_VERSION) != null && context.get(ContextConstants.ROUTE_VERSION) instanceof Version) {
                contextVersion = (Version) context.get(ContextConstants.ROUTE_VERSION);
                logEntry.setRouteVersion(contextVersion.toString());
            }

            logEntry.setResponseCode(String.valueOf(notNull(response.getStatus(), 0)));

            logEntry.setError(false);
            logEntry.setErrorMessage("");
            if (context.get(ContextConstants.ERROR_STATUS_CODE) != null) {
                logEntry.setError(true);
                logEntry.setResponseCode(String.valueOf((int) context.get(ContextConstants.ERROR_STATUS_CODE)));
                if (context.get(ContextConstants.ERROR_EXCEPTION) != null && context.get(ContextConstants.ERROR_EXCEPTION) instanceof Throwable) {
                    final Throwable throwable = (Throwable) context.get(ContextConstants.ERROR_EXCEPTION);
                    logEntry.setErrorMessage(throwable.getMessage());
                }
            }

            if (context.get(ContextConstants.STOPWATCH) != null && context.get(ContextConstants.STOPWATCH) instanceof StopWatch) {
                final StopWatch stopwatch = (StopWatch) context.get(ContextConstants.STOPWATCH);
                stopwatch.stop();
                logEntry.setResponseTime(stopwatch.getTotalTimeMillis());
            }

            logEntry.setEventName(ROUTER_EDGE_REQUEST);

            requestLogger.queueLogEntry(logEntry);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    private static String kvPair(String key, String value) {
        return new StringBuilder()
                .append(key)
                .append("=")
                .append(inQuotes(value)).toString();
    }

    private static String inQuotes(String value) {
        return new StringBuilder()
                .append("\"")
                .append(value)
                .append("\"").toString();
    }

    private static <T> T notNull(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
