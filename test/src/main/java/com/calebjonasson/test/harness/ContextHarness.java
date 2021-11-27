package com.calebjonasson.test.harness;

import com.calebjonasson.test.config.RateLimiterHarnessConfiguration;

public class ContextHarness {

	private final RateLimiterHarnessConfiguration configuration;

	private ContextHarness(final RateLimiterHarnessConfiguration configuration) {
		this.configuration = configuration;
	}

	public static ContextHarness of(RateLimiterHarnessConfiguration configuration) {
		return new ContextHarness(configuration);
	}








}