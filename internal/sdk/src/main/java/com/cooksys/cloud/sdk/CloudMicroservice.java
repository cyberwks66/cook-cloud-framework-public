package com.cooksys.cloud.sdk;

import com.cooksys.cloud.commons.event.EventConfiguration;
import com.cooksys.cloud.sdk.core.CloudSdkConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation required for an API to run on the Cloud framework.
 *
 * @author Steven Bradley
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@Import({CloudSdkConfiguration.class, EventConfiguration.class})
public @interface CloudMicroservice {

}
