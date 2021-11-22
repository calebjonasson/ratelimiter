package com.calebjonasson.ratelimiter.springboot.context;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class RedisContextProvider implements ContextProvider {
	private final ReactiveStringRedisTemplate redisTemplate;
	private Map<String, RateLimitContext> contexts = new HashMap<>();

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