package com.calebjonasson.ratelimiter.core.model.context;

import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractRateLimitContext<TYPE extends RateLimiterTypeStrategy> implements RateLimitContext<TYPE> {

	/**
	 * This is the context key to be passed into rate limiters
	 */
	protected String contextKey;

	protected boolean enabled = true;
}