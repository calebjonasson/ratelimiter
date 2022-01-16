package com.calebjonasson.ratelimiter.inmemory.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.limiter.AbstractRateLimiter;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;


/**
 * An in memory rate limiter.
 *
 * Call atomic() to perform a ratelimit operation.
 */
public abstract class InMemoryRateLimiter<
		TYPE extends RateLimiterTypeStrategy,
		CONTEXT extends AbstractRateLimitContext<TYPE>,
		STATE extends RateLimitState<TYPE>>
		extends AbstractRateLimiter<TYPE, CONTEXT, STATE> {

	/**
	 * Initialize the class with a context provider and a context configuration.
	 * @param contextProvider The context provider we are looking to add to the rate limiter.
	 */
	public InMemoryRateLimiter(final ContextProvider<TYPE, CONTEXT> contextProvider) {
		super(contextProvider);
	}

	@Override
	protected boolean isSelfPruning() {
		return false;
	}
}