package com.cooksys.cloud.sdk.timed;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface TimedTask {
    String name();

    String aggregate() default "[none]";
}
