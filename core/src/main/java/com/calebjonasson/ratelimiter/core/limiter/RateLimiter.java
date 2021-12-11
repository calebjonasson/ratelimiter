package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.request.RateLimitRequest;
import com.calebjonasson.ratelimiter.core.response.RateLimitHandleResponse;


/**
 * This is the definition of a rate limiter.
 */
public interface RateLimiter<R extends RateLimitRequest, C extends AbstractRateLimitContext> {

	/**
	 * Perform the rate limiting operation.
	 * @param rateLimitRequest A request container for the values required by the ratelimiter
	 * @return The rate limit state that was retrieved or newly created from this operation.
	 * @throws RateLimitException thrown if the state. Limit is exceeded.
	 */
	public RateLimitHandleResponse handle(R rateLimitRequest) throws RateLimitException;


	/**
	 * TODO: do we even need this? We can in theory have a NoContextProvider which has this turned off as default.
	 * Returns if this class is context based or not.
	 * @return false by default but other classes can override this value.
	 */
	public default boolean isContextBased() {
		return false;
	}

}