package com.calebjonasson.ratelimiter.core.context;

import com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;
import com.calebjonasson.ratelimiter.core.model.context.RateLimitContext;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

/**
 * This is a supplier for a rate limit context.
 *
 * It should be viable to store the rate limit context in memory or in a database. If it is stored in a database it will
 * need to be highly cached in order to perform effectively.
 *
 *
 * @author Caleb Jonasson
 */
public interface ContextProvider<
		TYPE extends RateLimiterTypeStrategy,
		CONTEXT extends RateLimitContext<TYPE>> {

	/**
	 * Load the context from whatever the data store is.
	 * @param contextKey The context key that we are loading from the datasource.
	 * @return An optional {@link AbstractRateLimitContext} if the implementation needs to create a context on the fly it should
	 * do so here.
	 * @throws RateLimitContextNotFoundException thrown if unable to find or create a context by the desired key.
	 */
	public <T extends RateLimitContext<TYPE>> T getContext(String contextKey) throws RateLimitContextNotFoundException;



	/**
	 * Get the configuration class that has been stored within this context.
	 * @return The configuration class that extends {@link AbstractContextConfiguration}
	 */
	public abstract <T extends AbstractContextConfiguration<TYPE>> T getContextConfiguration();
}