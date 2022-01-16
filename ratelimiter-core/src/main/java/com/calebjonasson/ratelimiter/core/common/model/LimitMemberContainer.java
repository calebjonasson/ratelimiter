package com.calebjonasson.ratelimiter.core.common.model;

/**
 * A shared interface to describe and decorate a class with requirements of a limit member.
 */
public interface LimitMemberContainer {

	/**
	 * get the limit of the atomic rate limiter.
	 * @return the limit of the rate limiter.
	 */
	public long getLimit();

	/**
	 * Set the limit of the atomic rate limiter
	 * @param limit The limit of the rate limiter.
	 */
	public void setLimit(long limit);

	/**
	 * Map the limit member from the passed in model to this.
	 * @param from The model we are mapping the limit from.
	 */
	public default void mapLimit(LimitMemberContainer from) {
		if(from == null) return;
		this.setLimit(from.getLimit());
	}
}