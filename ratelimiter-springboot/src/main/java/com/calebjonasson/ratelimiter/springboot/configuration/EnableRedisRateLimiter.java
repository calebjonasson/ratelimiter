package com.calebjonasson.ratelimiter.springboot.configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedisRateLimiterConfiguration.class)
public @interface EnableRedisRateLimiter {
}