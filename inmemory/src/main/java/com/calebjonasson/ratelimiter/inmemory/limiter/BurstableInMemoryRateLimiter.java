package com.calebjonasson.ratelimiter.inmemory.limiter;

import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;
import com.calebjonasson.ratelimiter.core.context.BurstableRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.context.configuration.BurstableContextConfiguration;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.response.RateLimitHandleResponse;
import com.calebjonasson.ratelimiter.core.state.BurstableRateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is a concrete implementation of a burstable ratelimiter.
 *
 */
public class BurstableInMemoryRateLimiter
		extends InMemoryRateLimiter<BurstableRateLimiterTypeStrategy, BurstableRateLimitContext, BurstableRateLimitState> {

	private Map<String, BurstableRateLimitState> states = new LinkedHashMap<>();

	/**
	 * Create a new In memory ratelimiter
	 *
	 * @param contextProvider The context data access to be used by the rate limiter.
	 */
	public BurstableInMemoryRateLimiter(ContextProvider contextProvider, BurstableContextConfiguration contextConfiguration) {
		super(contextProvider);
	}

	/**
	 * The main difference between the atomic and burstable is that the burstable finds it's capacity using a delta
	 * between now and the ttl then will find how much space can be used.
	 *
	 *
	 * @param context The context that we are performing an internal atomic increment under.
	 * @param stateKey The state key that we are updating.
	 * @param state The rate limit state that we are incrementing.
	 * @return
	 * @throws RateLimitException
	 */
	@Override
	protected synchronized RateLimitHandleResponse internalIncrement(BurstableRateLimitContext context, String stateKey, BurstableRateLimitState state)
			throws RateLimitException {

		long now = System.currentTimeMillis();
		long requestTokenCount = 1;

		float fillTime = context.getReplenishRate() < 1 ? 0 : context.getBurstCapacity() / (context.getReplenishRate());
		long ttl = (long)(fillTime * 2);
		long tokens = state.getTokens();
		long lastRefresh = state.getLastRefreshed() == null ? 0 : state.getLastRefreshed().getTime();
		long delta = Math.max(0, now - lastRefresh);

		// use the lowest of (delta and replenish rate) or burst capacity.
		// TODO: verify this doesn't need /1000
		long replenishTokens = tokens + ((delta * context.getReplenishRate()) / 1000);
		long fillTokens = Math.min(context.getBurstCapacity(), replenishTokens);

		if(fillTokens < requestTokenCount) {
			throw new RateLimitExceededException(context, state);
		}

		state.setTokens(fillTokens - requestTokenCount);
		state.refresh();

		// Update the state.
		this.states.put(stateKey, state);
		
		// Return the new state.
		return RateLimitHandleResponse.of(state);
	}

	@Override
	protected BurstableRateLimitState createRateLimitState(BurstableRateLimitContext context) {
		BurstableRateLimitState state = BurstableRateLimitState.builder()
				.tokens(context.getBurstCapacity())
				.build();

		return state;
	}

	@Override
	protected boolean isSelfPruning() {
		return false;
	}

	@Override
	protected boolean isValid(Optional<BurstableRateLimitState> burstableRateLimitState) {
		return burstableRateLimitState != null && burstableRateLimitState.isPresent();
	}

	@Override
	public Optional<BurstableRateLimitState> getRateLimitState(AbstractRateLimitContext context, String stateKey) {
		if(this.states.containsKey(stateKey)) {
			return Optional.of(this.states.get(stateKey));
		}

		return Optional.empty();
	}

	@Override
	protected void pruneExpiredStates() {

	}
}