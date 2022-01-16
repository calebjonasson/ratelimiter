package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.request.ContextStateRateLimitRequest;
import com.calebjonasson.ratelimiter.core.response.RateLimitHandleResponse;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

import java.util.Optional;

/**
 * This class will allow the storage of a rate limiter in redis as an atomic operation.
 *
 * @author Caleb Jonasson
 */
public abstract class AbstractRateLimiter<
		TYPE extends RateLimiterTypeStrategy,
		CONTEXT extends AbstractRateLimitContext<TYPE>,
		STATE extends RateLimitState<TYPE>>
		extends ContextBasedRateLimiter<TYPE, CONTEXT> {

	/**
	 * A context provider allowing the implementor to swap out a redis/in memory or otherwise.
	 */
	private final ContextProvider contextProvider;

	/**
	 * Store the ContextProvider in the {@link AbstractRateLimiter}
	 * @param contextProvider The context we are looking to store in this ratelimiter.
	 */
	public AbstractRateLimiter(ContextProvider<TYPE, CONTEXT> contextProvider) {
		this.contextProvider = contextProvider;
	}

	/**
	 * The is the concrete implementation of the overridden handle method.
	 * @param request The rate limit request we are looking to handle.
	 * @return A {@link RateLimitHandleResponse} containing the state and available capacity.
	 * @throws RateLimitException thrown if the rate limit is exceeded or another internal exception is thrown.
	 */
	public synchronized RateLimitHandleResponse handle(ContextStateRateLimitRequest request) throws RateLimitException {

		// Load the Current context/
		CONTEXT context = this.getContextProvider().getContext(request.getContextKey());

		// Check to see if the context allows for rate limiting or if it's blocked.
		if(!this.isContextEnable(context)) throw new RateLimitExceededException(context);

		// Get the state if present.
		Optional<STATE> optionalState = this.getRateLimitState(context, request.getStateKey());

		// Check to ensure that the state is valid.
		STATE state;
		if(this.isValid(optionalState)) {
			state = optionalState.get();
		} else {
			state = this.createRateLimitState(context);
		}

		// Check to see if we need to limit the rate based on the context options.
		return this.internalIncrement(context, request.getStateKey(), state);
	}

	/**
	 * This atomic increment needs to be atomic. It's up to the implementor to make it so.
	 * @param context The context that we are performing an internal atomic increment under.
	 * @param stateKey The state key that we are updating.
	 * @param state The rate limit state that we are incrementing.
	 * @return A RateLimitState
	 * @throws RateLimitExceededException Thrown if the atomic limit is exceeded.
	 */
	protected abstract RateLimitHandleResponse internalIncrement(CONTEXT context, String stateKey, STATE state)
			throws RateLimitException;

	/**
	 * Create a rate limit state from a context.
	 * @param context The context that we are creating a rate limit state for.
	 * @return a new instance of the RateLimitState
	 */
	protected abstract STATE createRateLimitState(CONTEXT context);

	/**
	 * Check to see is an Optionally wrapped state is valid.
	 * @param state The state that we are checking for validity.
	 * @return true if expires is not null and not expired.
	 */
	protected abstract boolean isValid(Optional<STATE> state);


	/**
	 * Implementation should either get the current state or create a new one.
	 * @param context The {@link AbstractRateLimitContext} we are loading a state key from within.
	 * @param stateKey The state key that we are looking to load.
	 * @return An instance of {@link RateLimitState} that cannot be null.
	 */
	public abstract Optional<STATE> getRateLimitState(AbstractRateLimitContext context, String stateKey);


	/**
	 * Check to see if a given rate limit context can have its values incremented.
	 * @param rateLimitContext The rate limit context that we are attempting to use.
	 * @return true if the context is enabled
	 */
	protected boolean isContextEnable(CONTEXT rateLimitContext) {
		// TODO: change this out for a means of getting the context enabled state from the provided context.
		return rateLimitContext != null && rateLimitContext.isEnabled();
	}

	/**
	 * Method will get a rate limit state
	 * @param contextKey The context we are loading.
	 * @param stateKey The state we are loading.
	 * @return an {@link Optional} wrapped RateLimitState if it's available.
	 * @throws RateLimitContextNotFoundException Thrown if we are unable to create a context or find desired context.
	 */
	public Optional<STATE> getRateLimitState(String contextKey, String stateKey) throws RateLimitContextNotFoundException {
		AbstractRateLimitContext rateLimitContext = this.getContextProvider().getContext(contextKey);
		return this.getRateLimitState(rateLimitContext, stateKey);
	}

	@Override
	protected ContextProvider<TYPE, CONTEXT> getContextProvider() {
		return this.contextProvider;
	}

	/**
	 * Method will prune the expired states from the state store.
	 *
	 * This api should not be called if this.isSelfPruning() returns true. If false, the implementor will need
	 * to self prune the states.
	 */
	protected abstract void pruneExpiredStates();

	/**
	 * Method will return if the implementation is self pruning.
	 * eg: a redis rate limiter that uses the Redis TTL will be self pruning so nothing will happen.
	 * @return true if there is no need to call `pruneExpiredState()`
	 */
	protected abstract boolean isSelfPruning();

}