package com.calebjonasson.ratelimiter.springboot.context;

import com.calebjonasson.ratelimiter.core.context.AtomicRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.AtomicContextConfiguration;
import com.calebjonasson.ratelimiter.core.type.strategy.AtomicRateLimiterTypeStrategy;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

public class AtomicRedisContextProvider extends RedisContextProvider<
		AtomicRateLimiterTypeStrategy,
		AtomicRateLimitContext,
		AtomicContextConfiguration> {

	/**
	 * Constructor that takes a redis template.
	 * @param redisTemplate The redis template
	 * @param configuration The configuration that is used to create contexts.
	 */
	public AtomicRedisContextProvider(final ReactiveRedisTemplate<String, AtomicRateLimitContext> redisTemplate, AtomicContextConfiguration configuration) {
		super(redisTemplate, configuration);
	}

	@Override
	protected AtomicRateLimitContext createContext(String contextKey) {
		return AtomicRateLimitContext.builder()
				.limit(this.contextConfiguration.getLimit())
				.interval(this.contextConfiguration.getInterval())
				.build();
	}
}