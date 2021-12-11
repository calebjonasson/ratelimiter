package com.calebjonasson.ratelimiter.core.context.configuration;

import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;
import lombok.Data;

/**
 * Configurations for burstable contexts.
 *
 * This class is responsible for setting defaults for things like burstable fill rates within rate limiters.
 */
@Data
public class BurstableContextConfiguration extends AbstractContextConfiguration<BurstableRateLimiterTypeStrategy> {


	/**
	 * The rate at which the burstable rate limiter will refresh.
	 */
	private final long replenishRate;

	/**
	 * The burst capacity that is the upper bound.
	 */
	private final long burstCapacity;

	/**
	 * Create a new {@link BurstableContextConfiguration}
	 * @param replenish The rate at which the burstable rate limiter will refresh.
	 * @param burstCapacity The burst capacity that is the upper bound.
	 */
	public BurstableContextConfiguration(long replenish, long burstCapacity) {
		this.replenishRate = replenish;
		this.burstCapacity = burstCapacity;
	}

	/**
	 * Create a new {@link BurstableContextConfiguration}
	 * @param replenish The rate at which the burstable rate limiter will refresh.
	 * @param burstCapacity The burst capacity that is the upper bound.
	 * @param createContextIfMissing A rule to override the default createContextIfMissing value.
	 */
	public BurstableContextConfiguration(long replenish, long burstCapacity, boolean createContextIfMissing) {
		this.replenishRate = replenish;
		this.burstCapacity = burstCapacity;
		this.createContextIfMissing = createContextIfMissing;
	}
}