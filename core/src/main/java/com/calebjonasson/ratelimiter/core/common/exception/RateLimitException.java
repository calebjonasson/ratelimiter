package com.calebjonasson.ratelimiter.core.common.exception;

/**
 * A common ratelimit exception that other exceptions should use as a base.
 */
public class RateLimitException extends Exception {

	private static final String DEFAULT_MESSAGE = "Unknown rate limit error occurred.";

	/**
	 * Default rate limit exceeded exception
	 */
	public RateLimitException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Create a new rate limit exception with a custom message.
	 * @param message A custom message
	 */
	public RateLimitException(String message) {
		super(message);
	}

	/**
	 * Create a new rate limit exception with an upstream exception.
	 * @param message A custom message.
	 * @param throwable An upstream error that occurred.
	 */
	public RateLimitException(String message, Throwable throwable) {
		super(message, throwable);
	}
}