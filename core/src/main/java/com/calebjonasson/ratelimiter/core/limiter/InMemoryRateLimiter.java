package com.calebjonasson.ratelimiter.core.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.model.RateLimitState;
import com.calebjonasson.ratelimiter.core.state.RateLimitStateFactory;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * An in memory rate limiter.
 */
public class InMemoryRateLimiter extends RateLimiter {

	/**
	 * A context provider allowing the implementor to swap out a redis/in memory or otherwise.
	 */
	private final ContextProvider contextDataAccess;


	/**
	 * The concrete states that are being held in the rate limiter.
	 * TODO: Swap this out to a linkedHashMap using an LRU data structure.
	 */
//	private Map<String, RateLimitState> states = new LinkedHashMap<>(40000, 5, true);
	private Map<String, RateLimitState> states = new HashMap<>();

	/**
	 * Create a new In memory ratelimiter
	 * @param contextDataAccess The context data access to be used by the rate limiter.
	 */
	public InMemoryRateLimiter(ContextProvider contextDataAccess) {
		this.contextDataAccess = contextDataAccess;
	}

	@Override
	protected void pruneExpiredStates() {

		// This can be improved upon. There are some heavy assumptions.
		for (Map.Entry<String, RateLimitState> entry : this.states.entrySet()) {
			Long now = System.currentTimeMillis();
			if(entry.getValue().getExpires().getTime() < now) {
				this.states.remove(entry.getKey());
			}
		}
	}

	@Override
	protected RateLimitState internalAtomicIncrement(RateLimitContext context, String stateKey, RateLimitState state) throws RateLimitExceededException {

		// Check to see if the current state has expired.
		if(state.getExpires().getTime() <= System.currentTimeMillis()) {
			state = RateLimitStateFactory.fromContext(context);
		}

		// Update the states atomic count in memory.
		if(context.getLimit() > 0 && state.getCount() < context.getLimit()) {
			state.setCount(state.getCount() + 1);
		}

		return this.states.put(stateKey, state);
	}

	@Override
	public Optional<RateLimitState> getRateLimitState(RateLimitContext context, String stateKey) {
		if(this.states != null && this.states.containsKey(stateKey)) {
			return Optional.of(this.states.get(stateKey));
		}
		return Optional.empty();
	}

	@Override
	protected ContextProvider getRateLimitContextDataAccess() {
		return this.contextDataAccess;
	}

	@Override
	protected boolean isSelfPruning() {
		return false;
	}
}