package com.calebjonasson.ratelimiter.core.response;

import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import lombok.Data;

/**
 * RateLimitHandleResponse is a container for interactions with the {@link com.calebjonasson.ratelimiter.core.limiter.RateLimiter}
 */
@Data
public class RateLimitHandleResponse {

	/**
	 * The state that is represented in memory, redis or a database.
	 */
	protected RateLimitState state;

	/**
	 * The space remaining.
	 */
	protected Long remainingCapacity;

	/**
	 *
	 * @param state The state to be stored in this.
	 */
	public RateLimitHandleResponse(RateLimitState state) {
		this.state = state;
	}

	/**
	 *
	 * @param state The state that is stored in memory, redis or aatabase.
	 * @param remainingCapacity The remaining request space.
	 */
	public RateLimitHandleResponse(RateLimitState state, Long remainingCapacity) {
		this.state = state;
		this.remainingCapacity = remainingCapacity;
	}

	/**
	 * Create a new {@link RateLimitHandleResponse} from a given state.
	 * @param state The state we want to return with the handle.
	 * @return A new {@link RateLimitHandleResponse}
	 */
	public static RateLimitHandleResponse of(RateLimitState state) {
		return new RateLimitHandleResponse(state);
	}

}