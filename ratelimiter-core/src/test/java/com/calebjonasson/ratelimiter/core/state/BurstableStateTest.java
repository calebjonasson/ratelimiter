package com.calebjonasson.ratelimiter.core.state;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class BurstableStateTest {

	@Test
	public void testBurstableStateRefreshSetsANewDateValue() {

		Date date = new Date();
		BurstableRateLimitState state = BurstableRateLimitState.builder().lastRefreshed(date).tokens(5l).build();

		state.refresh();

		Assertions.assertNotNull(state.getLastRefreshed());
		Assertions.assertFalse(date == state.getLastRefreshed());
	}
}