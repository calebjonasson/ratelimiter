package com.calebjonasson.ratelimiter.core.request;

import lombok.Data;

/**
 * A request that wraps a contextKey and a stateKey
 */
@Data
public class ContextStateRateLimitRequest implements RateLimitRequest {

	/**
	 * The context to use to group states under for configuration values.
	 */
	private final String contextKey;

	/**
	 * The state we are grouping requests under.
	 */
	private final String stateKey;

	/**
	 * Create a {@link ContextStateRateLimitRequest} with the provided context and state.
	 * @param contextKey The context key for the rate limiter.
	 * @param stateKey The state key for the rate limiter.
	 */
	public ContextStateRateLimitRequest(final String contextKey, final String stateKey) {
		this.contextKey = contextKey;
		this.stateKey = stateKey;
	}

	/**
	 * Create a {@link ContextStateRateLimitRequest} with the provided context and state.
	 * @param contextKey The context key for the rate limiter.
	 * @param stateKey The state key for the rate limiter.
	 * @return ContextStateRateLimitRequest
	 */
	public static ContextStateRateLimitRequest of(final String contextKey, final String stateKey) {
		return new ContextStateRateLimitRequest(contextKey, stateKey);
	}
}