package com.calebjonasson.ratelimiter.core.model.state;

import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

/**
 * TODO: extrapolate to an interface to define different data and core concepts that the rate limiter can support.
 *
 * eg: burstable will have an internal fill rate while an in memory approach may not use an expiry within the state
 * data structure. instead it can be outside and interacted with via the service.
 *
 * @see com.calebjonasson.ratelimiter.core.state.BurstableRateLimitState
 * @see com.calebjonasson.ratelimiter.core.state.AtomicRateLimitState
 */
public interface RateLimitState<TYPE extends RateLimiterTypeStrategy> {

}