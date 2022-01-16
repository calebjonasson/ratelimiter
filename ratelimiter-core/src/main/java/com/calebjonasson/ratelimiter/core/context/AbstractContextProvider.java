package com.calebjonasson.ratelimiter.core.context;

import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;
import com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration;
import com.calebjonasson.ratelimiter.core.model.context.RateLimitContext;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

import java.util.Optional;

/**
 * This abstract context provider is a simple class wrapper that contains the AbstractContextConfiguration
 * @param <CONTEXT> The type of context that this provider will be able to handle.
 * @param <CONFIGURATION> The configuration that this context provider will be able to handle.
 */
public abstract class AbstractContextProvider<
		TYPE extends RateLimiterTypeStrategy,
		CONTEXT extends RateLimitContext<TYPE>,
		CONFIGURATION extends AbstractContextConfiguration<TYPE>>
		implements ContextProvider<TYPE, CONTEXT> {

	/**
	 * The context configuration that we will access from the provider.
	 */
	protected final CONFIGURATION contextConfiguration;

	/**
	 * Create a new {@link AbstractContextProvider} from the provided configuration.
	 * @param contextConfiguration The context configuration.
	 */
	public AbstractContextProvider(final CONFIGURATION contextConfiguration) {
		this.contextConfiguration = contextConfiguration;
	}

	/**
	 * Get the configuration class that has been stored within this context.
	 * @return The configuration class that extends {@link AbstractContextConfiguration}
	 */
	public CONFIGURATION getContextConfiguration() {
		return this.contextConfiguration;
	}

	/**
	 * Get a context using the contextKey
	 * @param contextKey The context key that we are loading from the datasource.
	 * @return The Context if one is found, a new context if we setting is configured to do so. Otherwise exception.
	 * @throws RateLimitContextNotFoundException Thrown if we are unable to find the context and unable to create a new one.
	 */
	public CONTEXT getContext(String contextKey) throws RateLimitContextNotFoundException {

		Optional<CONTEXT> optionalContext = this.getContextInternal(contextKey);

		if(optionalContext.isPresent()) {
			return optionalContext.get();
		}else if(this.contextConfiguration.isCreateContextIfMissing()) {
			// If the configuration has a createContextIfMissing value we will create a context.
			return this.createContext(contextKey);
		}

		// Final scenario we will throw an exception.
		throw new RateLimitContextNotFoundException(contextKey);
	}

	/**
	 * Get the context from whatever the concrete implementation decides. memory, database, redis, etc..
	 * @param contextKey The context key we are looking to load.
	 * @return An optionally wrapped context.
	 */
	protected abstract Optional<CONTEXT> getContextInternal(String contextKey);

	/**
	 * The implementation of this method should simply instantiate a CONTEXT
	 * @param contextKey The key for the created context.
	 * @param <T> The type of context that this provider will be able to handle.
	 * @return A new context to be associated with the request.
	 */
	protected abstract <T extends RateLimitContext<TYPE>> T createContext(String contextKey);
}