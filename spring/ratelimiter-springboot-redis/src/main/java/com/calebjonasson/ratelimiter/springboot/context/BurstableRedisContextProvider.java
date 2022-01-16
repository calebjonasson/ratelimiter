package com.calebjonasson.ratelimiter.springboot.context;

import com.calebjonasson.ratelimiter.core.context.BurstableRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * A burstable in redis rate limit context storage.
 */
public class BurstableRedisContextProvider extends RedisContextProvider<
		BurstableRateLimiterTypeStrategy,
		BurstableRateLimitContext,
		BurstableContextConfiguration> {

	/**
	 * Create a burstable redis context provider.
 	 * @param redisTemplate The redis template that we will use to store contexts.
	 * @param contextConfiguration The configuration used to create contexts.
	 */
	public BurstableRedisContextProvider(ReactiveRedisTemplate<String, BurstableRateLimitContext> redisTemplate, BurstableContextConfiguration contextConfiguration) {
		super(redisTemplate, contextConfiguration);
	}

	@Override
	protected BurstableRateLimitContext createContext(String contextKey) {
		BurstableRateLimitContext context = BurstableRateLimitContext.builder()
				.replenishRate(this.contextConfiguration.getReplenishRate())
				.burstCapacity(this.contextConfiguration.getBurstCapacity())
				.build();
		return context;
	}
}