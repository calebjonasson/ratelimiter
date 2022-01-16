package com.calebjonasson.ratelimiter.inmemory.inmemory;


import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;
import com.calebjonasson.ratelimiter.core.context.configuration.ContextConfigurations;
import com.calebjonasson.ratelimiter.core.request.ContextStateRateLimitRequest;
import com.calebjonasson.ratelimiter.inmemory.context.BurstableInMemoryContextProvider;
import com.calebjonasson.ratelimiter.inmemory.limiter.BurstableInMemoryRateLimiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BurstableInMemoryRateLimiterTest {

	@Test
	public void testRateLimiterWithRefreshRate() throws RateLimitException {

		long replenishRate = 1;
		long burstCapacity = 5;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		String contextKey = "testRateLimiterWithRefreshRate";
		String stateKey = "get:/api/users/v1/user/123827367254";

		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
	}



	@Test
	public void testRateLimiterThrowsExceptionWhenCapacityIsExceeded() throws RateLimitException {

		long replenishRate = 1;
		long burstCapacity = 4;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		String contextKey = "testRateLimiterWithRefreshRate";
		String stateKey = "get:/api/users/v1/user/123827367254";

		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		Assertions.assertThrows(RateLimitExceededException.class, () -> rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey)));

	}

	@Test
	public void testRateLimiterThrowsExceptionWhenCapacityIsExceededWhenReplenishRateIsOne() throws RateLimitException {

		long replenishRate = 0;
		long burstCapacity = 4;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		String contextKey = "testRateLimiterWithRefreshRate";
		String stateKey = "get:/api/users/v1/user/123827367254";

		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		Assertions.assertThrows(RateLimitExceededException.class, () -> rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey)));

	}

	@Test
	public void testRateLimiterCanRefilAtACorrectRate() throws RateLimitException, InterruptedException {

		long replenishRate = 1;
		long burstCapacity = 4;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		String contextKey = "testRateLimiterWithRefreshRate";
		String stateKey = "get:/api/users/v1/user/123827367254";

		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));

		// We are refilling the capacity at a rate of 1 per second.
		Thread.sleep(2000);

		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));

		Assertions.assertThrows(RateLimitExceededException.class, () -> rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey)));
	}

	@Test
	public void testRateLimiterWithAReplenishRateOfZeroNeverRefils() throws RateLimitException, InterruptedException {

		long replenishRate = 0;
		long burstCapacity = 1;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		String contextKey = "testRateLimiterWithRefreshRate";
		String stateKey = "get:/api/users/v1/user/123827367254";

		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));

		// We are refilling the capacity at a rate of 1 per second.
		Thread.sleep(2000);
		Assertions.assertThrows(RateLimitExceededException.class, () -> rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey)));
	}

	@Test
	public void testRateLimiterDoesNotWorkWithoutABurstCapacity() throws RateLimitException {
		long replenishRate = 0;
		long burstCapacity = 0;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		String contextKey = "testRateLimiterWithRefreshRate";
		String stateKey = "get:/api/users/v1/user/123827367254";

		Assertions.assertThrows(RateLimitExceededException.class, () -> rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey)));
	}

}