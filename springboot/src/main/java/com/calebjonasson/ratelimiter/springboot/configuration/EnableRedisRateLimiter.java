package com.calebjonasson.ratelimiter.springboot.configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Add to a spring application to enable a redis rate limiter with default values.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedisRateLimiterConfiguration.class)
public @interface EnableRedisRateLimiter {
}