package com.calebjonasson.ratelimiter.springboot.context;

import com.calebjonasson.ratelimiter.core.context.AbstractContextProvider;
import com.calebjonasson.ratelimiter.core.context.configuration.AbstractContextConfiguration;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.type.strategy.RateLimiterTypeStrategy;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

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
public abstract class RedisContextProvider<
		TYPE extends RateLimiterTypeStrategy,
		CONTEXT extends AbstractRateLimitContext<TYPE>,
		CONFIGURATION extends AbstractContextConfiguration<TYPE>>
		extends AbstractContextProvider<TYPE, CONTEXT, CONFIGURATION> {

	/**
	 * The redis template in which operations will be performed against.
	 */
	private final ReactiveRedisTemplate<String, CONTEXT> redisTemplate;

	/**
	 * The contexts that are being stored in memory.
	 * TODO: remove this in favor of an actual redis context provider. This is just an in memory context provider..
	 */
	private Map<String, CONTEXT> contexts = new HashMap<>();

	/**
	 * Constructor that takes a redis template.
	 * @param redisTemplate The redis template
	 * @param configuration The configuration that is used to create contexts.
	 */
	public RedisContextProvider(final ReactiveRedisTemplate<String, CONTEXT> redisTemplate, CONFIGURATION configuration) {
		super(configuration);
		this.redisTemplate = redisTemplate;
	}

	@Override
	protected Optional<CONTEXT> getContextInternal(String contextKey) {
		return Optional.of(redisTemplate.opsForValue().get(contextKey).block());
	}

	/**
	 * Add a context.
	 * @param contextKey The context key to be added to the map.
	 * @param context The context that we are adding to the map.
	 * @return The added context.
	 */
	public AbstractRateLimitContext putContext(String contextKey, CONTEXT context) {
		this.contexts.put(contextKey, context);
		return context;
	}
}