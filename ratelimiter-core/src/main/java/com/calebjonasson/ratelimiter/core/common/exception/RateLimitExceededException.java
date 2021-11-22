package com.calebjonasson.ratelimiter.core.common.exception;

import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.model.RateLimitState;
import lombok.Data;

/**
 * An exception that should be thrown when a user exceeds the amount of atomic rate limits permitted by the context.
 *
 * This can be called with many options:
 * eg:
 * throw new RateLimitExceededException(context);
 *
 */
@Data
public class RateLimitExceededException extends RateLimitException {

	/**
	 * Default error message.
	 */
	private static final String DEFAULT_MESSAGE = "Rate limit has been exceeded.";

	/**
	 * The context is stored with the Exception to allow for access by implementing code.
	 */
	protected RateLimitContext context;

	/**
	 * The state is stored with the exception to allow for access by implementing code.
	 */
	protected RateLimitState state;

	/**
	 * Default constructor with the DEFAULT_MESSAGE
	 */
	public RateLimitExceededException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Standard constructor with a message.
	 * @param message A custom message to store with the exception.
	 */
	public RateLimitExceededException(String message) {
		super(message);
	}

	/**
	 * Constructor will store the state with the exception
	 * @param state The state we are looking to store with the exception.
	 */
	public RateLimitExceededException(RateLimitState state) {
		this();
		this.state = state;
	}

	/**
	 * Constructor will set the context and the state along with a custom message.
	 * @param state The state we are looking to store with the exception.
	 * @param message A custom message to store with the exception.
	 */
	public RateLimitExceededException(RateLimitState state, String message) {
		this(message);
		this.state = state;
	}

	/**
	 * Constructor will set the context and the state along with a custom message.
	 * @param context The context we are looking to store with the exception.
	 * @param state The state we are looking to store with the exception.
	 */
	public RateLimitExceededException(RateLimitContext context, RateLimitState state) {
		this();
		this.state = state;
		this.context = context;
	}

	/**
	 * Constructor will set the context and the state along with a custom message.
	 * @param context The context we are looking to store with the exception.
	 * @param state The state we are looking to store with the exception.
	 * @param message A custom message
	 */
	public RateLimitExceededException(RateLimitContext context, RateLimitState state, String message) {
		this(message);
		this.state = state;
		this.context = context;
	}

	/**
	 * Constructor will set the context as an available parameter.
	 * @param context The context to save with the exception
	 */
	public RateLimitExceededException(RateLimitContext context) {
		this();
		this.context = context;
	}

	/**
	 * Constructor will set the context and a custom message
	 * @param context The context to be saved with the exception
	 * @param message A custom message to be set.
	 */
	public RateLimitExceededException(RateLimitContext context, String message) {
		this(message);
		this.context = context;
	}

}