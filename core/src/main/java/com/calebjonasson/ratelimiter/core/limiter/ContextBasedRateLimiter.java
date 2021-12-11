package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.request.ContextStateRateLimitRequest;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

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