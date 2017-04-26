package com.cooksys.cloud.router.filters.pre;

import com.cooksys.cloud.router.core.ContextConstants;
import com.cooksys.cloud.router.core.RequestContextFactory;
import com.cooksys.cloud.router.filters.FilterOrderEnum;
import com.netflix.zuul.ZuulFilter;
import org.springframework.util.StopWatch;

/**
 * Zuul filter that starts a stopwatch and places it in the context - used by {@link com.cooksys.cloud.router.filters.post.RequestLoggingFilter}
 * to calculate round-trip response time
 *
 * @author Tim Davidson
 */
public class StopwatchFilter extends ZuulFilter {
    final RequestContextFactory contextFactory;

    public StopwatchFilter(RequestContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Override
    public String filterType() {
        return FilterOrderEnum.STOPWATCH_FILTER.getType();
    }

    @Override
    public int filterOrder() {
        return FilterOrderEnum.STOPWATCH_FILTER.getOrder();
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        contextFactory.getZuulRequestContext().put(ContextConstants.STOPWATCH, stopWatch);
        return null;
    }
}