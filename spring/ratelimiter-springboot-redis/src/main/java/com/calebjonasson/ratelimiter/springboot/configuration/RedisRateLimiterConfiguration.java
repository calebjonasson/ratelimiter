package com.calebjonasson.ratelimiter.springboot.configuration;

import com.calebjonasson.ratelimiter.core.context.BurstableRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;
import com.calebjonasson.ratelimiter.core.context.configuration.ContextConfigurations;
import com.calebjonasson.ratelimiter.springboot.context.BurstableRedisContextProvider;
import com.calebjonasson.ratelimiter.springboot.limiter.BurstableRedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
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
	@Autowired
	protected ReactiveStringRedisTemplate reactiveStringRedisTemplate;

	/**
	 * A redis template for context storage.
	 */
	@Autowired
	protected ReactiveRedisTemplate reactiveRedisTemplate;


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
	 * Create a burstable context configuration.
	 * @return the new context configuration.
	 */
	public BurstableContextConfiguration contextConfiguration() {
		return ContextConfigurations.burstableConfiguration(10, 20);
	}

	/**
	 * Create a default redis context provider from a redis template.
	 * @return A new instance of the {@link ContextProvider}
	 */
	@Bean
	public ContextProvider redisContextProvider() {
		BurstableRedisContextProvider contextProvider = new BurstableRedisContextProvider(this.reactiveRedisTemplate, this.contextConfiguration()) {
			@Override
			protected BurstableRateLimitContext createContext(String contextKey) {
				return BurstableRateLimitContext.builder()
						.burstCapacity(this.getContextConfiguration().getBurstCapacity())
						.replenishRate(this.getContextConfiguration().getReplenishRate())
						.build();
			}
		};
		return contextProvider;
	}

	/**
	 * Default {@link BurstableRedisRateLimiter}
	 * @return A new RedisRateLimiter
	 */
	@Bean
	public BurstableRedisRateLimiter redisRateLimiter() {
		BurstableRedisRateLimiter rateLimiter = new BurstableRedisRateLimiter(
				this.redisContextProvider(),
				this.reactiveStringRedisTemplate,
				this.redisRequestRateLimiterScript());
		return rateLimiter;
	}

}