package com.calebjonasson.ratelimiter.inmemory.limiter;


import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.context.AtomicRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.response.RateLimitHandleResponse;
import com.calebjonasson.ratelimiter.core.state.AtomicRateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.AtomicRateLimiterTypeStrategy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This rate limiter will support atomic operations that are non bursting for capacity changes.
 */
public class AtomicInMemoryRateLimiter
		extends InMemoryRateLimiter<AtomicRateLimiterTypeStrategy, AtomicRateLimitContext, AtomicRateLimitState> {


	/**
	 * The concrete states that are being held in the rate limiter.
	 * TODO: Swap this out to a linkedHashMap using an LRUCache data structure.
	 */
	private Map<String, AtomicRateLimitState> states = new HashMap<>();

	/**
	 * Create a new In memory ratelimiter
	 *
	 * @param contextProvider The context data access to be used by the rate limiter.
	 */
	public AtomicInMemoryRateLimiter(final ContextProvider<AtomicRateLimiterTypeStrategy, AtomicRateLimitContext> contextProvider) {
		super(contextProvider);
	}

	@Override
	protected RateLimitHandleResponse internalIncrement(AtomicRateLimitContext context, String stateKey, AtomicRateLimitState state)
			throws RateLimitExceededException {

		// Check to see if the current state has expired.
		if(state.getExpires().getTime() <= System.currentTimeMillis()) {
			state = this.createRateLimitState(context);
		}

		// Update the states atomic count in memory.
		if(context.getLimit() > 0 && state.getCount() < context.getLimit()) {
			state.setCount(state.getCount() + 1);
		}else {
			throw new RateLimitExceededException(context, state, "The context does not have a limit greater than 0 or exceeds the context limit.");
		}

		return RateLimitHandleResponse.of(this.states.put(stateKey, state));
	}

	@Override
	protected void pruneExpiredStates() {

		// This can be improved upon. There are some heavy assumptions.
		for (Map.Entry<String, AtomicRateLimitState> entry : this.states.entrySet()) {
			Long now = System.currentTimeMillis();
			if(entry.getValue().getExpires().getTime() < now) {
				this.states.remove(entry.getKey());
			}
		}
	}

	/**
	 * Check to see is an Optionally wrapped state is valid.
	 * @param state The state that we are checking for validity.
	 * @return true if expires is not null and not expired.
	 */
	protected boolean isValid(Optional<AtomicRateLimitState> state) {
		boolean valid = state != null
				&& state.isPresent()
				&& state.get().getExpires() != null
				&& state.get().getExpires().getTime() >= System.currentTimeMillis();

		return valid;
	}

	/**
	 * Create a rate limit state from a context.
	 * TODO: finalize this.
	 * @param context The context that we are creating a rate limit state for.
	 * @return a new instance of the RateLimitState
	 */
	protected AtomicRateLimitState createRateLimitState(AtomicRateLimitContext context) {
		return AtomicRateLimitState.builder()
				.expires(new Date(System.currentTimeMillis() + context.getInterval()))
				.build();
	}

	@Override
	public Optional<AtomicRateLimitState> getRateLimitState(AbstractRateLimitContext context, String stateKey) {
		if(this.states != null && this.states.containsKey(stateKey)) {
			return Optional.of(this.states.get(stateKey));
		}
		return Optional.empty();
	}
}