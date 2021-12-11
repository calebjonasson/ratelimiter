package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;
import com.calebjonasson.ratelimiter.core.context.AtomicRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.AtomicContextConfiguration;
import com.calebjonasson.ratelimiter.core.context.configuration.ContextConfigurations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AtomicInMemoryContextProviderTest {


	@Test
	public void testStoreContext() {
		String contextKey = "test-context-467823654hj";


		long limit = 10;
		long interval = 1000;

		AtomicContextConfiguration configuration = ContextConfigurations.atomicConfiguration(limit, interval);
		AtomicInMemoryContextProvider contextProvider = new AtomicInMemoryContextProvider(configuration);
		AtomicRateLimitContext context = contextProvider.createContext(contextKey);

		Assertions.assertEquals(limit, context.getLimit());
		Assertions.assertEquals(interval, context.getInterval());
		Assertions.assertEquals(configuration.isContextEnabled(), context.isEnabled());
	}

	@Test
	public void testManuallyAddedContextIsStoredAndUsedCorrectly() throws RateLimitContextNotFoundException {

		String contextKey = "test-context-467823654hj";
		long limit = 10;
		long interval = 1000;

		long updatedLimit = 2;
		long updatedInterval = 300;

		AtomicContextConfiguration configuration = ContextConfigurations.atomicConfiguration(limit, interval);
		AtomicInMemoryContextProvider contextProvider = new AtomicInMemoryContextProvider(configuration);
		AtomicRateLimitContext context = contextProvider.createContext(contextKey);

		// Update the context.
		context.setInterval(updatedInterval);
		context.setLimit(updatedLimit);
		contextProvider.putContext(contextKey, context);

		// fetch the updated context.
		AtomicRateLimitContext loadedContext = contextProvider.getContext(contextKey);

		Assertions.assertEquals(updatedLimit, loadedContext.getLimit());
		Assertions.assertEquals(updatedInterval, loadedContext.getInterval());
	}


	@Test
	public void testContextCreatesADefaultContextFromTheProvidedConfigurationIsContextNotFound() throws RateLimitContextNotFoundException {
		String contextKey = "missingContext";

		long limit = 10;
		long interval = 1000;

		AtomicContextConfiguration configuration = ContextConfigurations.atomicConfiguration(limit, interval);
		AtomicInMemoryContextProvider contextProvider = new AtomicInMemoryContextProvider(configuration);

		AtomicRateLimitContext context = contextProvider.getContext(contextKey);

		Assertions.assertNotNull(context, "context was expected but a null context was returned from the getContext() method.");
		Assertions.assertEquals(limit,    context.getLimit());
		Assertions.assertEquals(interval, context.getInterval());
	}

	@Test
	public void testContextThrowsContextMissingIfProvidedWithCorrectConfigurationAndMissingContextKey() {
		String contextKey = "missingContext";
		AtomicContextConfiguration configuration = ContextConfigurations.atomicConfiguration(10, 1000, false);
		AtomicInMemoryContextProvider contextProvider = new AtomicInMemoryContextProvider(configuration);

		// Expecting an exception
		Assertions.assertThrows(RateLimitContextNotFoundException.class, () -> contextProvider.getContext(contextKey));
	}

}