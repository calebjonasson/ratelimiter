package com.calebjonasson.ratelimiter.core.context;

import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;

import java.util.Optional;


/**
 * This is a supplier for a rate limit context.
 *
 * It should be viable to store the rate limit context in memory or in a database. If it is stored in a database it will
 * need to be highly cached in order to perform effectively.
 *
 *
 * @author Caleb Jonasson
 */
public interface ContextProvider {

	/**
	 * Load the context from whatever the data store is.
	 * @param contextKey The context key that we are loading from the datasource.
	 * @return An optional {@link RateLimitContext} if the implementation needs to create a context on the fly it should
	 * do so here.
	 */
	public Optional<RateLimitContext> getContext(String contextKey) throws RateLimitContextNotFoundException;

}