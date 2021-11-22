package com.calebjonasson.ratelimiter.factory;


import com.calebjonasson.ratelimiter.core.context.ContextProviderFactory;
import com.calebjonasson.ratelimiter.core.context.InMemoryContextProvider;
import com.calebjonasson.ratelimiter.core.limiter.RateLimiter;
import com.calebjonasson.ratelimiter.core.limiter.RateLimiterFactory;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimiterFactoryTest {

	@Test
	public void testInMemoryContextFromFactory() {

		String contextKey = "crawl.static.v1";
		String missingContextKey = "crawl.static.v2";
		RateLimitContext context = RateLimitContext.builder().limit(10).interval(1000).build();

		// Create the context.
		InMemoryContextProvider contextProvider = ContextProviderFactory.inMemoryContextProvider();
		contextProvider.putContext(contextKey, context);

		// Verify that we can retrieve the key that is stored in the context.
		assertTrue(contextProvider.getContext(contextKey).isPresent());
		assertFalse(contextProvider.getContext(missingContextKey).isPresent());
	}

	@Test
	public void testCanCreateInMemoryRateLimiter() {
		String contextKey = "crawl.static.v1";
		String missingContextKey = "crawl.static.v2";
		RateLimitContext context = RateLimitContext.builder().limit(10).interval(1000).build();

		// Create the context.
		InMemoryContextProvider contextProvider = ContextProviderFactory.inMemoryContextProvider();
		contextProvider.putContext(contextKey, context);

		// Create a new rate limiter from the context.
		RateLimiter rateLimiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);
	}
}