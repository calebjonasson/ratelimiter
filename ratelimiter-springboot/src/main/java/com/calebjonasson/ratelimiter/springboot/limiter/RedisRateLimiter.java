package com.calebjonasson.ratelimiter.springboot.limiter;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.limiter.RateLimiter;
import com.calebjonasson.ratelimiter.core.model.RateLimitContext;
import com.calebjonasson.ratelimiter.core.model.RateLimitState;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.*;

/**
 * A redis implementation of the rate limiter.
 *
 * TODO: Implement the pruning operation although this isn't really needed because of the built in ttl.
 */
@Log4j2
public class RedisRateLimiter extends RateLimiter {

	private static final String REDIS_PROPERTY_TOKENS = "tokens";
	private static final String REDIS_PROPERTY_TIMESTAMP = "timestamp";

	protected final ContextProvider contextProvider;
	protected final ReactiveStringRedisTemplate redisTemplate;
	protected final RedisScript redisScript;

	public RedisRateLimiter(
			final ContextProvider contextProvider,
			final ReactiveStringRedisTemplate redisTemplate,
			final RedisScript redisScript) {
		this.contextProvider = contextProvider;
		this.redisTemplate = redisTemplate;
		this.redisScript = redisScript;
	}

	/**
	 * Generate a redis key prefix.
	 * @param contextKey The context key that we are looking to generate.
	 * @param stateKey The specific state that we are creating a key with.
	 * @return a string that resembles `<contextKey>.{<stateKey>}` without the `<>`
	 */
	private String redisKeyPrefix(String contextKey, String stateKey) {
		return contextKey+".{" + stateKey + "}";
	}


	@Override
	protected ContextProvider getRateLimitContextDataAccess() {
		return this.contextProvider;
	}

	@Override
	public Optional<RateLimitState> getRateLimitState(RateLimitContext context, String stateKey) {

		String index = this.redisKeyPrefix(context.getContextKey(), stateKey) + "." + REDIS_PROPERTY_TOKENS;
		String value = this.redisTemplate.opsForValue().get(index).block();

		if(!Strings.isBlank(value)) {
			RateLimitState result = RateLimitState.builder().count(Long.parseLong(value)).build();
			return Optional.ofNullable(result);
		}
		return Optional.empty();
	}

	@Override
	protected RateLimitState internalAtomicIncrement(RateLimitContext context, String stateKey, RateLimitState state) throws RateLimitException {

		// Make a unique key per user.
		String prefix = this.redisKeyPrefix(context.getContextKey(), stateKey);

		// You need two Redis keys for Token Bucket.
		String tokenKey = prefix + "." + REDIS_PROPERTY_TOKENS;
		String timestampKey = prefix + "." + REDIS_PROPERTY_TIMESTAMP;

		List tokens = Arrays.asList(tokenKey, timestampKey);

		try {
			int replenishRate = 1;
			int burstCapacity = (int)context.getLimit();
			int requestedTokens = 1;
			// The arguments to the LUA script. time() returns unixtime in seconds.
			List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
					Instant.now().getEpochSecond() + "", requestedTokens + "");

			// allowed, tokens_left = redis.eval(SCRIPT, keys, args)
			Flux<List<Long>> flux = this.redisTemplate.execute(this.redisScript, tokens, scriptArgs);

			ArrayList<Long> redisResult = flux.onErrorResume(throwable -> {
				log.info("error calling rate limiter lua", throwable);
				if (log.isDebugEnabled()) {
					log.debug("Error calling rate limiter lua", throwable);
				}
				return Flux.just(Arrays.asList(1L, -1L));
			}).reduce(new ArrayList<Long>(), (longs, l) -> {
				log.info("reduce longs={}", l);
				longs.addAll(l);
				return longs;
			}).block();

			if(redisResult.get(0) != 1L) throw new RateLimitExceededException();

			return RateLimitState.builder().count(redisResult.get(1)).build();
		}
		catch (Exception e) {
			/*
			 * We don't want a hard dependency on Redis to allow traffic. Make sure to set
			 * an alert so you know if this is happening too much. Stripe's observed
			 * failure rate is 0.01%.
			 */
			throw new RateLimitException("An error occurred during rate limiting.", e);
		}
	}

	@Override
	protected void pruneExpiredStates() {
		// This is going to do nothing because of how redis already supports a ttl data structure and will prune itself.
	}


	@Override
	protected boolean isSelfPruning() {
		return true;
	}
}