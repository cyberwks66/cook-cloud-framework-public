package com.cooksys.cloud.router.filters.route.ribbon;


/**
 * Stores an instance of RibbonFilterContext that is scoped to the current request context (current thread)
 *
 * @author Tim Davidson
 */
public class RibbonFilterContextFactory {

    private static final ThreadLocal<RibbonFilterContext> contextHolder = new InheritableThreadLocal<RibbonFilterContext>() {
        @Override
        protected RibbonFilterContext initialValue() {
            return new DefaultRibbonFilterContext();
        }
    };

    public static RibbonFilterContext getCurrentContext() {
        return contextHolder.get();
    }

    public static void clearCurrentContext() {
        contextHolder.remove();
    }
}
