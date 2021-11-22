package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;

/**
 * A utility class that allows for the creation of rate limiters via a factory method pattern.
 */
public class RateLimiterFactory {

	/**
	 * Factory method to create a new in memory rate limiter
	 * @param contextDataAccess The context data access that needs to be injected into the rate limiter.
	 * @return A new {@link InMemoryRateLimiter}
	 */
	public static InMemoryRateLimiter inMemoryRateLimiter(ContextProvider contextDataAccess) {
		return new InMemoryRateLimiter(contextDataAccess);
	}



}