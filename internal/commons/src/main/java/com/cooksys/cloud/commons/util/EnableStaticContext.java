package com.cooksys.cloud.commons.util;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convienience annotation that imports {@link CommonContextUtilConfiguration}
 *
 * @author Tim Davidson
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CommonContextUtilConfiguration.class)
public @interface EnableStaticContext {
}
