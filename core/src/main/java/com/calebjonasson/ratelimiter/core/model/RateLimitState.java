package com.calebjonasson.ratelimiter.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * TODO: extrapolate to an interface to define different data and core concepts that the rate limiter can support.
 *
 * eg: burstable will have an internal fill rate while an in memory approach may not use an expiry within the state
 * data structure. instead it can be outside and interacted with via the service.
 */
@Data
@Builder
public class RateLimitState {
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
				", expires.diff=" + (expires.getTime() - System.currentTimeMillis())+
				", count=" + count +
				'}';
	}
}