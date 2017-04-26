package com.cooksys.cloud.router.filters;

/**
 * Defines filter order and type for zuul filters
 *
 * @author Tim Davidson
 */
public enum FilterOrderEnum {

    // Convention used here for filter orders: pre 1-99, route 100-199, post 200-299
    STOPWATCH_FILTER(3,FilterType.PRE),
    SIMPLE_ROUTE_LOCATOR_FILTER(5,FilterType.PRE),
    PRE_DECORATION_FILTER(10, FilterType.PRE),
    DYNAMIC_PRE_FILTER_CHAIN_FILTER(50, FilterType.PRE),
    TRAFFIC_RATIO_FILTER(60, FilterType.PRE),
    LOAD_BALANCED_ROUTING_FILTER(100, FilterType.ROUTE),
    SIMPLE_ROUTING_FILTER(101,FilterType.ROUTE),
    REQUEST_LOGGING_FILTER(1001,FilterType.POST); // will execute after spring cloud's SendResponseFilter

    private final int order;
    private final String type;

    FilterOrderEnum(int order, String type) {
        this.order=order;
        this.type=type;
    }
    public int getOrder() {
        return order;
    }


    public String getType() {
        return type;
    }

    private static class FilterType {
        private static final String ROUTE = "route";
        private static final String PRE = "pre";
        private static final String POST = "post";
    }
}
