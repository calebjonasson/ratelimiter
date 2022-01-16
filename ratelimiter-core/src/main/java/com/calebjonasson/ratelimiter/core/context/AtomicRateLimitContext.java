package com.calebjonasson.ratelimiter.core.context;

import com.calebjonasson.ratelimiter.core.common.model.IntervalMemberContainer;
import com.calebjonasson.ratelimiter.core.common.model.LimitMemberContainer;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.type.strategy.AtomicRateLimiterTypeStrategy;

import lombok.Builder;
import lombok.Data;

/**
 * A rate limit context that will contain and perform atomic operations.
 */
@Data
@Builder
public class AtomicRateLimitContext
		extends AbstractRateLimitContext<AtomicRateLimiterTypeStrategy>
		implements LimitMemberContainer, IntervalMemberContainer {


	/**
	 * The default amount of time for a given context state to have once created.
	 */
	public static final long DEFAULT_INTERVAL = (1000 * 60); // 1 minute

	/**
	 * The default amount of rates a context can hold over the interval.
	 */
	public static final long DEFAULT_LIMIT = 10;

	/**
	 * The limit of requests to be made within a given interval.
	 */
	private long limit;

	/**
	 * The amount of time in miliseconds that we will use to set a TTL.
	 */
	private long interval;
}