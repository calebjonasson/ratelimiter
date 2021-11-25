package com.calebjonasson.ratelimiter.springboot.context;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The redis context provider will provide a context as a rate limiting structure.
 *
 * This is used to group like things together and load configurations from.
 *
 * eg: rest-api-ratelimiter context will have rules attached to it:
 * tll
 * limit
 * ... To be expanded on.
 *
 *
 */
public class RedisContextProvider implements ContextProvider {

	/**
	 * The redis template in which operations will be performed against.
	 */
	private final ReactiveStringRedisTemplate redisTemplate;

	/**
	 * The contexts that are being stored in memory.
	 * TODO: remove this in favor of an actual redis context provider. This is just an in memory context provider..
	 */
	private Map<String, RateLimitContext> contexts = new HashMap<>();

	/**
	 * Constructor that takes a redis template.
	 * @param redisTemplate The redist template
	 */
	public RedisContextProvider(final ReactiveStringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Optional<RateLimitContext> getContext(String contextKey) {
		if (this.contexts.containsKey(contextKey)) {
			return Optional.of(this.contexts.get(contextKey));
		} else {

			RateLimitContext context = RateLimitContext.builder().contextKey(contextKey).limit(10).interval(Duration.ofSeconds(5).toMillis()).build();
			return Optional.of(this.putContext(contextKey, context));
		}
	}

	/**
	 * Add a context.
	 * @param contextKey The context key to be added to the map.
	 * @param context The context that we are adding to the map.
	 * @return The added context.
	 */
	public RateLimitContext putContext(String contextKey, RateLimitContext context) {
		this.contexts.put(contextKey, context);
		return context;
	}
}