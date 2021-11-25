package com.calebjonasson.ratelimiter.core.inmemory;

import com.calebjonasson.ratelimiter.core.context.ContextProviderFactory;
import com.calebjonasson.ratelimiter.core.context.InMemoryContextProvider;
import com.calebjonasson.ratelimiter.core.limiter.InMemoryRateLimiter;
import com.calebjonasson.ratelimiter.core.limiter.RateLimiterFactory;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.model.RateLimitState;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryRateLimiterTest {

	private static final String DEFAULT_CONTEXT_KEY = "test-context-1";

	@Test
	public void testSuccessfulRateLimitingWithinBounds() throws RateLimitException {

		InMemoryContextProvider contextProvider = ContextProviderFactory.inMemoryContextProvider();
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, RateLimitContext.builder().limit(2).interval(5000).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		final String stateKey = "test-1";

		for(int x = 0; x < 2; x++) {
			limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
		}
	}

	@Test
	public void testLimitExceededException() throws RateLimitException, InterruptedException {

		InMemoryContextProvider contextProvider = ContextProviderFactory.inMemoryContextProvider();
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, RateLimitContext.builder().limit(5).interval(5000).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		final String stateKey = "test-1";

		// May need a way of creating a context.
		Assertions.assertThrows(RateLimitExceededException.class, () -> {
			for(int x = 0; x < 7; x++) {
				limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
				Thread.sleep(100);
			}
		});

		RateLimitContext rateLimitContext = contextProvider.getContext(DEFAULT_CONTEXT_KEY)
				.orElseThrow(IllegalStateException::new);

		RateLimitState rateLimitState = limiter.getRateLimitState(rateLimitContext, stateKey)
				.orElseThrow(IllegalStateException::new);

		Assertions.assertEquals(5, rateLimitState.getCount());
	}

	@Test
	public void testOnExpiresANewStateIsAdded() throws RateLimitException, InterruptedException {

		InMemoryContextProvider contextProvider = ContextProviderFactory.inMemoryContextProvider();
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, RateLimitContext.builder().limit(3).interval(3000).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		final String stateKey = "test-1";

		limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
		limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
		limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);


		// Sleep for enough time to reset the interval.
		Thread.sleep(3100);

		limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
		limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
		limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);

		Assertions.assertThrows(RateLimitExceededException.class, () -> limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey));
	}

	/**
	 * Test the accuracy of multithreaded operations.
	 * In order to ensure that this is accurately working in a multithreaded environment we will
	 * need to send many requests at the same time and check for accuracy of stored items vs state.
	 */
	@Test
	public void testAccuracyWithMultiThreadedOperations() {

		int limit = 100;
		int interval = 3000;
		final String stateKey = "test-1";
		int atomicOperations = 10000;
		List<Integer> operations = new ArrayList();

		InMemoryContextProvider contextProvider = ContextProviderFactory.inMemoryContextProvider();
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, RateLimitContext.builder().limit(limit).interval(interval).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		// Create a list of atomic operations.
		for(int x = 0; x < atomicOperations; x++) {
			operations.add(x);
		}

		AtomicInteger successCount = new AtomicInteger();

		// Once we have loaded the operations we will attempt to break the rate limiter.
		operations.stream().parallel().peek(x -> {
			try {
				limiter.atomic(DEFAULT_CONTEXT_KEY, stateKey);
				successCount.getAndIncrement();
			} catch (RateLimitException e) {
				// We are expecting to hit exceptions here so this is ok. We don't want to log the 9900 so; do nothing.
			}

		}).collect(Collectors.toList());

		// Assert that we are only performing mutex operations.
		Assertions.assertEquals(limit, successCount.get());
	}
}