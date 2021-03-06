package com.calebjonasson.ratelimiter.core.context.configuration;

import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;
import lombok.Data;

/**
 * A context configuration that will allow for basic configuration of Contexts.
 * @param <TYPE> The rate limiter type used to link together like implementations.
 */
@Data
public abstract class AbstractContextConfiguration<
		TYPE extends RateLimiterTypeStrategy> {

	/**
	 * Create a context if missing.
	 * TODO: validate that this is the best way to handle feature switches. May need to change to an enum set
	 * in the future.
	 */
	protected boolean createContextIfMissing = true;

	/**
	 * A switch to turn all rate limiting contexts off.
	 */
	protected boolean contextEnabled = true;
}