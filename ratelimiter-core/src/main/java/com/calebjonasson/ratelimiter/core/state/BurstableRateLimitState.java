package com.calebjonasson.ratelimiter.core.state;

import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * A bustrable rate limit state that will use tokens and a last refreshed to know what it can burst up to.
 */
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
	 * @return The current {@link BurstableRateLimitState}
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