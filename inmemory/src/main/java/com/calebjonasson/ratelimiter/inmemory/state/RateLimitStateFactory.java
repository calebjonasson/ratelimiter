package com.calebjonasson.ratelimiter.inmemory.state;

import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.model.RateLimitState;

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
	public static RateLimitState fromContext(RateLimitContext rateLimitContext) {
		return RateLimitState.builder()
				.expires(new Date(System.currentTimeMillis() + rateLimitContext.getInterval()))
				.build();
	}
}