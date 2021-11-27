package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Basic in memory {@link ContextProvider}
 */
public class InMemoryContextProvider implements ContextProvider {

	private Map<String, RateLimitContext> contexts = new HashMap<>();

	@Override
	public Optional<RateLimitContext> getContext(String contextKey) {
		if (this.contexts.containsKey(contextKey)) {
			return Optional.of(this.contexts.get(contextKey));
		}
		return Optional.empty();
	}

	/**
	 * Add a context.
	 * @param contextKey The context key to be added to the map.
	 * @param context The context that we are adding to the map.
	 */
	public void putContext(String contextKey, RateLimitContext context) {
		this.contexts.put(contextKey, context);
	}

	/**
	 * Method will build a {@link InMemoryContextProvider}
	 * This is not a very good implementation and using the RedisContextDataAccess
	 * @return Call the {@link ContextProviderFactory} and use it's factory method to create a new {@link ContextProvider}.
	 */
	public static InMemoryContextProvider build() {
		return ContextProviderFactory.inMemoryContextProvider();
	}
}