package com.calebjonasson.ratelimiter.springboot;


import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.springboot.configuration.EnableRedisRateLimiter;
import com.calebjonasson.ratelimiter.springboot.limiter.RedisRateLimiter;
import com.calebjonasson.ratelimiter.springboot.shared.RatelimiterSampleSpringApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

@PropertySource("application.yml")
@EnableRedisRateLimiter
@SpringBootTest(classes = RatelimiterSampleSpringApplication.class)
public class SpringbootRatelimiterRedisConfigurationAutoloaderTest {

	@Autowired
	protected RedisRateLimiter redisRateLimiter;

	@Autowired
	protected ContextProvider redisContextProvider;

	@Test
	public void testApplicationContextContainsRedisRateLimiter() {
		Assertions.assertNotNull(this.redisRateLimiter, "The RedisRateLimiter bean was null and not automatically created.");
	}

	@Test
	public void testApplicationContextContainsRedisRateContextProvider() {
		Assertions.assertNotNull(this.redisContextProvider, "The RedisContextProvider bean was null and not automatically created.");
	}

}