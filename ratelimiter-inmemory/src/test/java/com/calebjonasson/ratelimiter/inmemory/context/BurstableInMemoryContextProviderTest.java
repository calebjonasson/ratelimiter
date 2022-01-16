package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;
import com.calebjonasson.ratelimiter.core.context.BurstableRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;
import com.calebjonasson.ratelimiter.core.context.configuration.ContextConfigurations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BurstableInMemoryContextProviderTest {

	@Test
	public void testStoreContext() {
		String contextKey = "test-context-467823654hj";


		long replenishRate = 10;
		long burstCapacity = 1000;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableRateLimitContext context = contextProvider.createContext(contextKey);

		Assertions.assertEquals(replenishRate, context.getReplenishRate());
		Assertions.assertEquals(burstCapacity, context.getBurstCapacity());
		Assertions.assertEquals(configuration.isContextEnabled(), context.isEnabled());
	}

	@Test
	public void testManuallyAddedContextIsStoredAndUsedCorrectly() throws RateLimitContextNotFoundException {

		String contextKey = "test-context-467823654hj";
		long replenishRate = 10;
		long burstCapacity = 1000;

		long updatedReplenishRate = 10;
		long updatedBurstCapacity = 1000;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableRateLimitContext context = contextProvider.createContext(contextKey);

		// Update the context.
		context.setBurstCapacity(updatedBurstCapacity);
		context.setReplenishRate(updatedReplenishRate);
		contextProvider.putContext(contextKey, context);

		// fetch the updated context.
		BurstableRateLimitContext loadedContext = contextProvider.getContext(contextKey);

		Assertions.assertEquals(updatedBurstCapacity, loadedContext.getBurstCapacity());
		Assertions.assertEquals(updatedReplenishRate, loadedContext.getReplenishRate());
	}


	@Test
	public void testContextCreatesADefaultContextFromTheProvidedConfigurationIsContextNotFound() throws RateLimitContextNotFoundException {
		String contextKey = "missingContext";

		long replenishRate = 10;
		long burstCapacity = 1000;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);
		BurstableRateLimitContext context = contextProvider.createContext(contextKey);

		Assertions.assertNotNull(context, "context was expected but a null context was returned from the getContext() method.");
		Assertions.assertEquals(replenishRate, context.getReplenishRate());
		Assertions.assertEquals(burstCapacity, context.getBurstCapacity());
	}

	@Test
	public void testContextThrowsContextMissingIfProvidedWithCorrectConfigurationAndMissingContextKey() {
		String contextKey = "missingContext";
		long replenishRate = 10;
		long burstCapacity = 1000;

		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity, false);
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);

		// Expecting an exception
		Assertions.assertThrows(RateLimitContextNotFoundException.class, () -> contextProvider.getContext(contextKey));
	}

}