package com.calebjonasson.ratelimiter.core.context;

import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;
import lombok.Builder;
import lombok.Data;

/**
 * A context for the burstable rate limit type.
 */
@Data
@Builder
public class BurstableRateLimitContext extends AbstractRateLimitContext<BurstableRateLimiterTypeStrategy> {


	/**
	 * The rate at which the burstable rate limiter will refresh.
	 */
	private long replenishRate;

	/**
	 * The burst capacity that is the upper bound.
	 */
	private long burstCapacity;

}