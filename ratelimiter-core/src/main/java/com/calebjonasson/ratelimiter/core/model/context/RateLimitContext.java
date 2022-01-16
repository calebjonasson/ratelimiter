package com.calebjonasson.ratelimiter.core.model.context;

import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;

/**
 * A rate limit context interface requiring a type strategy to be passed in to group like implementations.
 * @param <TYPE> The strategy of rate limiter to be used.
 */
public interface RateLimitContext<TYPE extends RateLimiterTypeStrategy> {
}