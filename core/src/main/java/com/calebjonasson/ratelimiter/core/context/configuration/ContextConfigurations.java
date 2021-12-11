package com.calebjonasson.ratelimiter.core.context.configuration;

/**
 * Factory class that contains some simple {@link AbstractContextConfiguration} implementations for standard use cases.
 *
 * It's encouraged to use your own as some of the bursting will depend on your web service implementation and the amount
 * of calls to rate limiting that is done on initial page load.
 *
 * eg: initial page load contains 30 api calls that go through the same ratelimiter context. you will exceed your limit.
 *
 */
public class ContextConfigurations {

	/**
	 * Pass in a custom burstable configuration.
	 * @param replenishRate The replenish rate of the limiting factor.
	 * @param burstCapacity The amount of total capacity the rate limiter has for the given rate.
	 * @return A {@link BurstableContextConfiguration}
	 */
	public static BurstableContextConfiguration burstableConfiguration(long replenishRate, long burstCapacity) {
		return new BurstableContextConfiguration(replenishRate, burstCapacity);
	}

	/**
	 * Pass in a custom burstable configuration.
	 * @param replenishRate The replenish rate of the limiting factor.
	 * @param burstCapacity The amount of total capacity the rate limiter has for the given rate.
	 * @param createContextIfMissing A rule to override the default createContextIfMissing value.
	 * @return A {@link BurstableContextConfiguration}
	 */
	public static BurstableContextConfiguration burstableConfiguration(long replenishRate, long burstCapacity, boolean createContextIfMissing) {
		return new BurstableContextConfiguration(replenishRate, burstCapacity, createContextIfMissing);
	}

	/**
	 * Create a custom atomic configuration
	 * @param limit The limit of requests we want to support in a given window.
	 * @param interval The interval of limit reset.
	 * @return A new {@link AtomicContextConfiguration}
	 */
	public static AtomicContextConfiguration atomicConfiguration(long limit, long interval) {
		return new AtomicContextConfiguration(limit, interval);
	}

	/**
	 * Create a custom atomic configuration
	 * @param limit The limit of requests we want to support in a given window.
	 * @param interval The interval of limit reset.
	 * @param createContextIfMissing A rule to override the default createContextIfMissing value.
	 * @return A new {@link AtomicContextConfiguration}
	 */
	public static AtomicContextConfiguration atomicConfiguration(long limit, long interval, boolean createContextIfMissing) {
		return new AtomicContextConfiguration(limit, interval, createContextIfMissing);
	}

}