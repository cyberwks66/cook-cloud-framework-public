package com.cooksys.cloud.commons.leaderelection;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convienence annotation that imports {@link LeaderElectionConfiguration}
 *
 * @author Tim Davidson
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LeaderElectionConfiguration.class)
public @interface EnableLeaderElection {
}
