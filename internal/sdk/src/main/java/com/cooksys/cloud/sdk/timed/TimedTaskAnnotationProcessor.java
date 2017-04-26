package com.cooksys.cloud.sdk.timed;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Component
public class TimedTaskAnnotationProcessor implements InitializingBean {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private Map<Method, TimedTask> annotatedMethods;

    private final Map<String, Set<String>> taskIndex = new HashMap<String, Set<String>>();

    public void processAnnotation() {

        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(EnableTimer.class));

        for (final BeanDefinition bd : scanner.findCandidateComponents("com.cooksys.cloud")) {
            logger.info("@EnableTimer annotation found on class: " + bd.getBeanClassName());

            Class<?> clazz = null;

            try {
                clazz = Class.forName(bd.getBeanClassName());
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            annotatedMethods = new HashMap<Method, TimedTask>();

            // Traverse interfaces, check for annotated methods
            for (final Class<?> interfaze : clazz.getInterfaces()) {
                for (final Method method : interfaze.getMethods()) {
                    if (method.isAnnotationPresent(TimedTask.class)) {
                        try {
                            annotatedMethods.put(clazz.getMethod(method.getName(), method.getParameterTypes()),
                                    method.getAnnotation(TimedTask.class));
                        } catch (NoSuchMethodException | SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Check the class itself
            for (final Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(TimedTask.class)) {
                    annotatedMethods.put(method, method.getAnnotation(TimedTask.class));
                }
            }

            // Build an index so StopwatchAggregator can quickly access all the
            // task names
            for (final Entry<Method, TimedTask> entry : annotatedMethods.entrySet()) {
                taskIndex.put(entry.getValue().aggregate(), new HashSet<String>());
            }
            for (final Entry<Method, TimedTask> entry : annotatedMethods.entrySet()) {
                taskIndex.get(entry.getValue().aggregate()).add(entry.getValue().name());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        processAnnotation();

    }

    public Map<Method, TimedTask> getAnnotatedMethods() {
        return annotatedMethods;
    }

    public Map<String, Set<String>> getTaskIndex() {
        return taskIndex;
    }

}
