package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.context.AtomicRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.AtomicContextConfiguration;
import com.calebjonasson.ratelimiter.core.type.strategy.AtomicRateLimiterTypeStrategy;

/**
 * Concrete implementation of an atomic version of the in memory context provider.
 */
public class AtomicInMemoryContextProvider extends InMemoryContextProvider<
		AtomicRateLimiterTypeStrategy,
		AtomicRateLimitContext,
		AtomicContextConfiguration> {

	/**
	 * Constructor that takes a {@link com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration}
	 *
	 * @param contextConfiguration The context we are looking to store in the context provider.
	 */
	public AtomicInMemoryContextProvider(AtomicContextConfiguration contextConfiguration) {
		super(contextConfiguration);
	}

	@Override
	protected AtomicRateLimitContext createContext(String contextKey) {
		return AtomicRateLimitContext.builder()
				.limit(this.contextConfiguration.getLimit())
				.interval(this.contextConfiguration.getInterval())
				.build();
	}
}