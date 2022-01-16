package com.calebjonasson.ratelimiter.core.state;

import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class BurstableRateLimitState implements RateLimitState<BurstableRateLimiterTypeStrategy> {


	/**
	 * The modified date for the rate limit state.
	 */
	protected Date lastRefreshed;

	/**
	 * How many tokens are left in the burstable rate limit state.
	 */
	protected Long tokens;


	/**
	 * Update the states refresh timestamp with the current date.
	 */
	public BurstableRateLimitState refresh() {
		this.setLastRefreshed(new Date());
		return this;
	}


	@Override
	public String toString() {
		return "RateLimitState{" +
				"lastRefresh=" + this.lastRefreshed +
				", lastRefresh.diff=" + (this.lastRefreshed == null ? 0 : this.lastRefreshed.getTime() - System.currentTimeMillis())+
				", tokens=" + tokens +
				'}';
	}
}