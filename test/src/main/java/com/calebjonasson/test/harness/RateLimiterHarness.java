package com.calebjonasson.test.harness;

import com.calebjonasson.ratelimiter.core.limiter.RateLimiter;
import com.calebjonasson.test.config.RateLimiterHarnessConfiguration;

/**
 * The RateLimiterHarness is used to test rate limiters in a standardized way that matches up to the interface definition.
 */
public class RateLimiterHarness {


	/**
	 * The configuration we will use to work with the rate limiter
	 */
	private final RateLimiterHarnessConfiguration configuration;

	/**
	 * The rate limiter we are looking to test with this harness.
	 */
	private final RateLimiter rateLimiter;

	/**
	 * private constructor to create a ratelimiter harness.
	 *
	 * Use RateLimiterHarness.of(...)
	 *
	 * @param configuration The configuration we will use to work with the rate limiter.
	 * @param rateLimiter The rate limiter to be tested by this harness.
	 */
	private RateLimiterHarness(final RateLimiterHarnessConfiguration configuration, final RateLimiter rateLimiter) {
		this.configuration = configuration;
		this.rateLimiter = rateLimiter;
	}

	/**
	 * Static creation of the rate limiter harness
	 * @param configuration The configuration we will use to work with the rate limiter.
	 * @param rateLimiter The rate limiter to be tested by this harness.
	 * @return The harness that will work with the tests.
	 */
	public static RateLimiterHarness of(RateLimiterHarnessConfiguration configuration, RateLimiter rateLimiter) {
		return new RateLimiterHarness(configuration, rateLimiter);
	}
}