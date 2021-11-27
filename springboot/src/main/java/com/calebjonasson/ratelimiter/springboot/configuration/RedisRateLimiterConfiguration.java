package com.calebjonasson.ratelimiter.springboot.configuration;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.springboot.context.RedisContextProvider;
import com.calebjonasson.ratelimiter.springboot.limiter.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * This is the default configuration used for redis rate limiting.
 */
@Configuration
public class RedisRateLimiterConfiguration {

	/**
	 * The reactive redis template used to store the state of the rate limiter.
	 */
	@Autowired protected ReactiveStringRedisTemplate redisTemplate;


	/**
	 * Configuration for a RedisScript
	 * @return a new instance of {@link RedisScript}
	 */
	@Bean
	@SuppressWarnings("unchecked")
	public RedisScript redisRequestRateLimiterScript() {
		DefaultRedisScript redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
		redisScript.setResultType(List.class);
		return redisScript;
	}

	/**
	 * Create a default redis context provider from a redis template.
	 * @return A new instance of the {@link ContextProvider}
	 */
	@Bean
	public ContextProvider redisContextProvider() {
		RedisContextProvider contextProvider = new RedisContextProvider(this.redisTemplate);
		return contextProvider;
	}

	/**
	 * Default {@link RedisRateLimiter}
	 * @return A new RedisRateLimiter
	 */
	@Bean
	public RedisRateLimiter redisRateLimiter() {
		RedisRateLimiter rateLimiter = new RedisRateLimiter(
				this.redisContextProvider(),
				this.redisTemplate,
				this.redisRequestRateLimiterScript());
		return rateLimiter;
	}

}