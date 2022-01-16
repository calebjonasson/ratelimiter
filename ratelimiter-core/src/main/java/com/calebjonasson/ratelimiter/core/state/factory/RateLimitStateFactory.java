package com.calebjonasson.ratelimiter.core.state.factory;

import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;

import java.util.Date;


/**
 * This is a utility class that contains factory methods to easily create a rate limit state context.
 */
public class RateLimitStateFactory {

	/**
	 * Method will create a state from a given context.
	 * @param rateLimitContext The rate limit context that contains the duration of the rate limiter.
	 * @return A new instance of a {@link RateLimitState} object
	 */
//	public static RateLimitState fromContext(AbstractRateLimitContext rateLimitContext) {
//		return RateLimitState.builder()
//				.expires(new Date(System.currentTimeMillis() + rateLimitContext.getInterval()))
//				.build();
//	}
}