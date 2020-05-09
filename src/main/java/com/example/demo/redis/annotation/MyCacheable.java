package com.example.demo.redis.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Author: linjinghui
 * Date: 2020/5/7
 * Time: 15:44
 * Description: No Description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyCacheable {

    boolean cacheable() default true;

    String value() default "";

    String key() default "";

    long timeOut() default 0;

    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
