package com.calebjonasson.ratelimiter.core.context;

/**
 * A static factory util class that will allow for the creation of an in memory context provider.
 */
public class ContextProviderFactory {

	/**
	 * Create a new In Memory Context Data Access
	 * @return A new {@link InMemoryContextProvider}
	 */
	public static InMemoryContextProvider inMemoryContextProvider() {
		return new InMemoryContextProvider();
	}
}