package com.calebjonasson.ratelimiter.inmemory.model;

import lombok.Builder;
import lombok.Data;

/**
 * A rate limit context is the configuration per key prefix.
 *
 * Ideally we will be able to do the following with rules.
 * * set a limit of how many counts per x
 *
 *
 * @author Caleb Jonasson
 */

@Data
@Builder
public class RateLimitContext {

	/**
	 * The default amount of time for a given context state to have once created.
	 */
	public static final long DEFAULT_INTERVAL = (1000 * 60); // 1 minute

	/**
	 * The default amount of rates a context can hold over the interval.
	 */
	public static final long DEFAULT_LIMIT = 10;

	/**
	 * The amount of time given to complete the limit of operations.
	 */
	protected long interval = DEFAULT_INTERVAL;

	/**
	 * The amount of operations that can be performed within a given interval.
	 */
	protected long limit = DEFAULT_LIMIT;

	/**
	 * The context key. This can likely be removed.
	 * TODO: validate that this can be removed.
	 */
	protected String contextKey;
}