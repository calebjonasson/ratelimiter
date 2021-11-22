package com.calebjonasson.ratelimiter.core.common.exception;

/**
 * Exception can be thrown if the context provider does not automatically create a new context for missing contexts.
 */
public class RateLimitContextNotFoundException extends RateLimitException {

	private static final String DEFAULT_MESSAGE = "Unable to find Rate Limit Context.";
	private static final String DEFAULT_CONTEXT_KEY_MESSAGE = "Unable to find RateLimitContext by contextKey=%s";

	/**
	 * Default exception thrown if we require a context be available for a given ratelimiter.
	 */
	public RateLimitContextNotFoundException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Throw a context not found exception with a context key as a reference.
	 * @param contextKey The context key we are making the implementor aware of via the message member.
	 */
	public RateLimitContextNotFoundException(String contextKey) {
		super(String.format(DEFAULT_CONTEXT_KEY_MESSAGE, contextKey));
	}
}