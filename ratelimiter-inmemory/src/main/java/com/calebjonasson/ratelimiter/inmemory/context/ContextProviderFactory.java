package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.context.configuration.AtomicContextConfiguration;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;

/**
 * A static factory util class that will allow for the creation of an in memory context provider.
 */
public class ContextProviderFactory {

	/**
	 * Create a new In Memory Context Data Access
	 * @param contextConfiguration the context that we want to associate with the {@link InMemoryContextProvider}
	 * @return A new {@link InMemoryContextProvider}
	 */
	public static AtomicInMemoryContextProvider atomicInMemoryContextProvider(AtomicContextConfiguration contextConfiguration) {
		return new AtomicInMemoryContextProvider(contextConfiguration);
	}

	/**
	 * Create a burstable in memory rate limiter.
	 * @param contextConfiguration The context configuration that will be used to create and modify context creation.
	 * @return A new instance of the {@link BurstableInMemoryContextProvider}
	 */
	public static BurstableInMemoryContextProvider burstableInMemoryContextProvider(BurstableContextConfiguration contextConfiguration) {
		return new BurstableInMemoryContextProvider(contextConfiguration);
	}
}