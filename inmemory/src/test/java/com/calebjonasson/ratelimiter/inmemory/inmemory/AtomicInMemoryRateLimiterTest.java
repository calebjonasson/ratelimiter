package com.calebjonasson.ratelimiter.inmemory.inmemory;

import com.calebjonasson.ratelimiter.core.context.AtomicRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.AtomicContextConfiguration;
import com.calebjonasson.ratelimiter.core.context.configuration.ContextConfigurations;
import com.calebjonasson.ratelimiter.core.request.ContextStateRateLimitRequest;
import com.calebjonasson.ratelimiter.core.state.AtomicRateLimitState;
import com.calebjonasson.ratelimiter.inmemory.context.AtomicInMemoryContextProvider;
import com.calebjonasson.ratelimiter.inmemory.context.ContextProviderFactory;
import com.calebjonasson.ratelimiter.inmemory.context.InMemoryContextProvider;
import com.calebjonasson.ratelimiter.inmemory.limiter.AtomicInMemoryRateLimiter;
import com.calebjonasson.ratelimiter.inmemory.limiter.InMemoryRateLimiter;
import com.calebjonasson.ratelimiter.inmemory.limiter.RateLimiterFactory;
import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AtomicInMemoryRateLimiterTest {

	private static final String DEFAULT_CONTEXT_KEY = "test-context-1";

	private static AtomicContextConfiguration atomicContextConfiguration =
			ContextConfigurations.atomicConfiguration(5, 5000);

	@Test
	public void testSuccessfulRateLimitingWithinBounds() throws RateLimitException {

		InMemoryContextProvider contextProvider = ContextProviderFactory.atomicInMemoryContextProvider(this.atomicContextConfiguration);
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, AtomicRateLimitContext.builder().limit(2).interval(5000).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		final String stateKey = "test-1";

		for(int x = 0; x < 2; x++) {
			limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
		}
	}

	@Test
	public void testLimitExceededException() throws Throwable {

		AtomicInMemoryContextProvider contextProvider = ContextProviderFactory.atomicInMemoryContextProvider(this.atomicContextConfiguration);
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, AtomicRateLimitContext.builder().limit(5).interval(5000).build());
		AtomicInMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		final String stateKey = "test-1";

		// May need a way of creating a context.
		Assertions.assertThrows(RateLimitExceededException.class, () -> {
			for(int x = 0; x < 7; x++) {
				limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
				Thread.sleep(100);
			}
		});

		AtomicRateLimitContext rateLimitContext = contextProvider.getContext(DEFAULT_CONTEXT_KEY);

		AtomicRateLimitState rateLimitState = limiter.getRateLimitState(rateLimitContext, stateKey)
				.orElseThrow(IllegalStateException::new);

		Assertions.assertEquals(5, rateLimitState.getCount());
	}

	@Test
	public void testOnExpiresANewStateIsAdded() throws RateLimitException, InterruptedException {

		long limit = 3;
		long interval = 1000;
		long intervalPadding = 100;

		InMemoryContextProvider contextProvider = ContextProviderFactory.atomicInMemoryContextProvider(this.atomicContextConfiguration);
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, AtomicRateLimitContext.builder().limit(limit).interval(interval).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		final String stateKey = "test-1";

		limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
		limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
		limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));

		// Sleep for enough time to reset the interval.
		Thread.sleep(interval + intervalPadding);

		limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
		limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
		limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));

		Assertions.assertThrows(RateLimitExceededException.class, () -> limiter.handle(
				ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey))
		);
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
		int atomicOperations = 4000;
		List<Integer> operations = new ArrayList();

		InMemoryContextProvider contextProvider = ContextProviderFactory.atomicInMemoryContextProvider(ContextConfigurations.atomicConfiguration(limit, interval));
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, AtomicRateLimitContext.builder().limit(limit).interval(interval).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		// Create a list of atomic operations.
		for(int x = 0; x < atomicOperations; x++) {
			operations.add(x);
		}

		AtomicInteger successCount = new AtomicInteger();

		// Once we have loaded the operations we will attempt to break the rate limiter.
		operations.stream().parallel().peek(x -> {
			try {
				limiter.handle(ContextStateRateLimitRequest.of(DEFAULT_CONTEXT_KEY, stateKey));
				successCount.getAndIncrement();
			} catch (RateLimitException e) {
				// We are expecting to hit exceptions here so this is ok. We don't want to log the 9900 so; do nothing.
			}

		}).collect(Collectors.toList());

		// Assert that we are only performing mutex operations.
		Assertions.assertEquals(limit, successCount.get());
	}
}