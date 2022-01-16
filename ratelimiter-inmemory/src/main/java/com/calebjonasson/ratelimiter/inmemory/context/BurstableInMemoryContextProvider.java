package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.context.BurstableRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;

/**
 * This is the burstable implementation of an in memory context provider.
 *
 * It is stored in memory and follows the burstable flow for creating and configuring contexts.
 *
 */
public class BurstableInMemoryContextProvider extends InMemoryContextProvider<
		BurstableRateLimiterTypeStrategy,
		BurstableRateLimitContext,
		BurstableContextConfiguration> {


	/**
	 * Constructor that takes a {@link com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration}
	 *
	 * @param contextConfiguration The context we are looking to store in the context provider.
	 */
	public BurstableInMemoryContextProvider(BurstableContextConfiguration contextConfiguration) {
		super(contextConfiguration);
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