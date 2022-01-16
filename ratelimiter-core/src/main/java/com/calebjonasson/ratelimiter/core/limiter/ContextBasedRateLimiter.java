package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.request.ContextStateRateLimitRequest;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

/**
 * A context based rate limiter will group like requests under a given context and allow for data to be loaded and used
 * from within the provided context.
 * @param <TYPE> The type of rate limiter used to group like implementations.
 * @param <CONTEXT> The context that is required during implementation.
 */
public abstract class ContextBasedRateLimiter<
			TYPE extends RateLimiterTypeStrategy,
			CONTEXT extends AbstractRateLimitContext<TYPE>
		>
		implements RateLimiter<ContextStateRateLimitRequest, CONTEXT> {

	/**
	 * A Ratelimiter will need access to a given context in order to know about said contexts rules.
	 * @return An instance of the ContextProvider
	 */
	protected abstract ContextProvider<TYPE, CONTEXT> getContextProvider();

	@Override
	public boolean isContextBased() {
		return true;
	}
}