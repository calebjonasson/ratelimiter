package com.calebjonasson.ratelimiter.inmemory.context;

import com.calebjonasson.ratelimiter.core.context.AbstractContextProvider;
import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration;
import com.calebjonasson.ratelimiter.core.model.context.RateLimitContext;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Basic in memory {@link ContextProvider}
 */
public abstract class InMemoryContextProvider<
			TYPE extends RateLimiterTypeStrategy,
			CONTEXT extends RateLimitContext<TYPE>,
			CONFIGURATION extends AbstractContextConfiguration<TYPE>
		>
		extends AbstractContextProvider<TYPE, CONTEXT, CONFIGURATION> {

	private Map<String, CONTEXT> contexts = new HashMap<>();


	/**
	 * Constructor that takes a {@link AbstractContextConfiguration}
	 * @param contextConfiguration The context we are looking to store in the context provider.
	 */
	public InMemoryContextProvider(final CONFIGURATION contextConfiguration) {
		super(contextConfiguration);
	}

	@Override
	protected Optional<CONTEXT> getContextInternal(String contextKey) {
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
	public void putContext(String contextKey, CONTEXT context) {
		this.contexts.put(contextKey, context);
	}
}