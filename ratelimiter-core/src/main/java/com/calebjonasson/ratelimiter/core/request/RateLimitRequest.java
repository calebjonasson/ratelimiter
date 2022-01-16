package com.calebjonasson.ratelimiter.core.request;


/**
 * The base interface for a rate limit request.
 *
 * This interface is implemented and allows for type safe changes in parameters for each concrete RateLimiter.
 *
 * @see ContextStateRateLimitRequest as an example of this.
 */
public interface RateLimitRequest {
}