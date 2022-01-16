package com.calebjonasson.ratelimiter.core.context.configuration;

import com.calebjonasson.ratelimiter.core.common.model.IntervalMemberContainer;
import com.calebjonasson.ratelimiter.core.common.model.LimitMemberContainer;
import com.calebjonasson.ratelimiter.core.type.strategy.AtomicRateLimiterTypeStrategy;
import lombok.Data;

/**
 * Basic atomic context configuration values.
 */
@Data
public class AtomicContextConfiguration
		extends AbstractContextConfiguration<AtomicRateLimiterTypeStrategy>
		implements IntervalMemberContainer, LimitMemberContainer {

	/**
	 * The limit of requests to be made within a given interval.
	 */
	protected long limit;

	/**
	 * The amount of time in miliseconds that we will use to set a TTL.
	 */
	protected long interval;

	/**
	 *
	 * @param limit The amount of requests per interval duration.
	 * @param interval The amount of time in milliseconds.
	 */
	public AtomicContextConfiguration(long limit, long interval) {
		this.limit = limit;
		this.interval = interval;
	}

	/**
	 *
	 * @param limit The amount of requests per interval duration.
	 * @param interval The amount of time in milliseconds.
	 * @param createContextIfMissing A rule to override the default createContextIfMissing value.
	 */
	public AtomicContextConfiguration(long limit, long interval, boolean createContextIfMissing) {
		this.limit = limit;
		this.interval = interval;
		this.createContextIfMissing = createContextIfMissing;
	}
}