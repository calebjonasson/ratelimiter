package com.calebjonasson.ratelimiter.core.state;

import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.AtomicRateLimiterTypeStrategy;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * A state opposite of the {@link BurstableRateLimitState} that is used to store an expiration as
 * well as an atomic counter.
 */
@Data
@Builder
public class AtomicRateLimitState implements RateLimitState<AtomicRateLimiterTypeStrategy> {

	/**
	 * The expires date when the state will be reset.
	 */
	protected Date expires;

	/**
	 * The amount of atomic increments that have happened for this state.
	 */
	protected long count = 0;

	@Override
	public String toString() {
		return "RateLimitState{" +
				"expires=" + expires +
				", expires.diff=" + (expires == null ? 0 : expires.getTime() - System.currentTimeMillis())+
				", count=" + count +
				'}';
	}
}