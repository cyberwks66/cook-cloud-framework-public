package com.cooksys.cloud.sdk;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Utility class that gives access to the Spring application context from within
 * non managed pojos.
 *
 * TODO: remove this, as there is a duplicate class found in commons library
 *
 * @author Tim Davidson
 */

public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContext.context = context;
    }

    /**
     * Returns the spring application context
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * Returns a bean from spring application context by name
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return (context==null) ? null : context.getBean(beanName);
    }
}