package com.calebjonasson.ratelimiter.core.common.model;

/**
 * This interface is used to require like implementations of the interval property so we can easily map values.
 */
public interface IntervalMemberContainer {

	/**
	 * Get the interval value.
	 * @return The amount of time in miliseconds that we will use to set a TTL.
	 */
	public long getInterval();

	/**
	 * Set the interval value
	 * @param interval The amount of time in miliseconds that we will use to set a TTL.
	 */
	public void setInterval(long interval);


	/**
	 * Map the interval from the object passed in. Do nothing if null.
	 * @param from The value we are getting the interval member from.
	 */
	public default void mapInterval(IntervalMemberContainer from) {
		if(from == null) return;
		this.setInterval(from.getInterval());
	}


}