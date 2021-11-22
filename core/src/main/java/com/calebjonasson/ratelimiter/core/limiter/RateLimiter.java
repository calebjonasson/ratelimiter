package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.model.RateLimitState;
import com.calebjonasson.ratelimiter.core.state.RateLimitStateFactory;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitContextNotFoundException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;

import java.util.Optional;

/**
 * This class will allow the storage of a rate limiter in redis as an atomic operation.
 *
 * @author Caleb Jonasson
 */
public abstract class RateLimiter {

	/**
	 * increment the rate limit atomic count.
	 * @param contextKey The context that we are access the rate via.
	 * @param stateKey The key of the state we are looking to load.
	 * @throws RateLimitException thrown if the state.limit is exceeded.
	 * @return The rate limit state that was retrieved or newly created from this operation.
	 */
	public synchronized RateLimitState atomic(String contextKey, String stateKey) throws RateLimitException {

		// Load the Current context/
		RateLimitContext context = this.getRateLimitContextDataAccess()
				.getContext(contextKey)
				.orElseThrow(() -> new RateLimitContextNotFoundException());

		// Check to see if the context allows for rate limiting or if it's blocked.
		if(!this.isContextEnable(context)) throw new RateLimitExceededException();

		// Get the state if present.
		Optional<RateLimitState> optionalState = this.getRateLimitState(context, stateKey);
		RateLimitState state;

		// Check to ensure that the state is valid.
		if(this.isValid(optionalState)) {
			state = optionalState.get();
		} else {
			state = this.createRateLimitState(context);
		}

		// Check to see if we are exceeding the rate limit.
		if(context.getLimit() == 0) {
			throw new RateLimitExceededException(context, "RateLimitContext does not support any atomic operations.");
		}else if(state.getCount() >= context.getLimit()) {
			throw new RateLimitExceededException(context, state, "RateLimitState.count exceeds the RateLimitContext.count");
		}

		// Check to see if we need to limit the rate based on the context options.
		return this.internalAtomicIncrement(context, stateKey, state);
	}

	/**
	 * This atomic increment needs to be atomic. It's up to the implementor to make it so.
	 * @param context The context that we are performing an internal atomic increment under.
	 * @param stateKey The state key that we are updating.
	 * @param state The rate limit state that we are incrementing.
	 * @return A RateLimitState
	 * @throws RateLimitExceededException Thrown if the atomic limit is exceeded.
	 */
	protected abstract RateLimitState internalAtomicIncrement(RateLimitContext context, String stateKey, RateLimitState state)
			throws RateLimitException;

	/**
	 * Check to see if a given rate limit context can have its values incremented.
	 * @param rateLimitContext The rate limit context that we are attempting to use.
	 * @return true if the context is enabled
	 */
	protected boolean isContextEnable(RateLimitContext rateLimitContext) {
		long limit = rateLimitContext.getLimit();
		return rateLimitContext != null && (limit > 0 || limit == -1);
	}

	/**
	 * Create a rate limit state from a context.
	 * @param context The context that we are creating a rate limit state for.
	 * @return a new instance of the RateLimitState
	 */
	protected RateLimitState createRateLimitState(RateLimitContext context) {
		RateLimitState state = RateLimitStateFactory.fromContext(context);
		return state;
	}

	/**
	 * Check to see is an Optionally wrapped state is valid.
	 * @param state The state that we are checking for validity.
	 * @return true if expires is not null and not expired.
	 */
	protected boolean isValid(Optional<RateLimitState> state) {
		return state != null
				&& state.isPresent()
				&& state.get().getExpires() != null
				&& state.get().getExpires().getTime() > System.currentTimeMillis();
	}

	/**
	 * Implementation should either get the current state or create a new one.
	 * @param context The {@link RateLimitContext} we are loading a state key from within.
	 * @param stateKey The state key that we are looking to load.
	 * @return An instance of {@link RateLimitState} that cannot be null.
	 */
	public abstract Optional<RateLimitState> getRateLimitState(RateLimitContext context, String stateKey);


	/**
	 * Method will get a rate limit state
	 * @param contextKey The context we are loading.
	 * @param stateKey The state we are loading.
	 * @return an {@link Optional} wrapped RateLimitState if it's available.
	 * @throws RateLimitContextNotFoundException Thrown if we are unable to create a context or find desired context.
	 */
	public Optional<RateLimitState> getRateLimitState(String contextKey, String stateKey) throws RateLimitContextNotFoundException {
		RateLimitContext rateLimitContext = this.getRateLimitContextDataAccess()
				.getContext(contextKey)
				.orElseThrow(() -> new RateLimitContextNotFoundException(contextKey));

		return this.getRateLimitState(rateLimitContext, stateKey);
	}

	/**
	 * This is used to load the {@link ContextProvider}
	 * @return The context provider that was injected into this class.
	 */
	protected abstract ContextProvider getRateLimitContextDataAccess();

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