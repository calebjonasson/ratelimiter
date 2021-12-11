package com.calebjonasson.ratelimiter.springboot.limiter;

import com.calebjonasson.ratelimiter.core.context.BurstableRateLimitContext;
import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.core.limiter.AbstractRateLimiter;
import com.calebjonasson.ratelimiter.core.model.context.AbstractRateLimitContext;
import com.calebjonasson.ratelimiter.core.model.state.RateLimitState;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitExceededException;
import com.calebjonasson.ratelimiter.core.common.exception.RateLimitException;

import com.calebjonasson.ratelimiter.core.response.RateLimitHandleResponse;
import com.calebjonasson.ratelimiter.core.state.BurstableRateLimitState;
import com.calebjonasson.ratelimiter.core.type.strategy.BurstableRateLimiterTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.*;

/**
 * A redis implementation of the rate limiter.
 *
 * TODO: Implement the pruning operation although this isn't really needed because of the built in ttl.
 */
public class BurstableRedisRateLimiter<
		CONTEXT extends BurstableRateLimitContext>
		extends AbstractRateLimiter<BurstableRateLimiterTypeStrategy, CONTEXT, BurstableRateLimitState> {

	private static final Logger log = LoggerFactory.getLogger(BurstableRedisRateLimiter.class);
	private static final String REDIS_PROPERTY_TOKENS = "tokens";
	private static final String REDIS_PROPERTY_TIMESTAMP = "timestamp";

	/**
	 * The redis template provided by spring.
	 */
	protected final ReactiveStringRedisTemplate redisTemplate;

	/**
	 * Used to perform lua operations against the redis template.
	 */
	protected final RedisScript redisScript;

	/**
	 * Create a new concrete redis rate limiter.
	 * @param contextProvider The context we want to use for the rate limiter state configuration.
	 * @param redisTemplate The redis template that is used to perform redis operations.
	 * @param redisScript The redis lua script used to perform burstable rate limiting.
	 */
	public BurstableRedisRateLimiter(
			final ContextProvider contextProvider,
			final ReactiveStringRedisTemplate redisTemplate,
			final RedisScript redisScript) {
		super(contextProvider);
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
	public Optional<BurstableRateLimitState> getRateLimitState(AbstractRateLimitContext context, String stateKey) {

		String index = this.redisKeyPrefix(context.getContextKey(), stateKey) + "." + REDIS_PROPERTY_TOKENS;
		String value = this.redisTemplate.opsForValue().get(index).block();

		if(!StringUtils.hasText(value)) {
			BurstableRateLimitState result = BurstableRateLimitState.builder().tokens(Long.parseLong(value)).build();
			return Optional.ofNullable(result);
		}
		return Optional.empty();
	}

	@Override
	protected RateLimitHandleResponse internalIncrement(CONTEXT context, String stateKey, BurstableRateLimitState state)
			throws RateLimitException {

		// Make a unique key per user.
		String prefix = this.redisKeyPrefix(context.getContextKey(), stateKey);

		// You need two Redis keys for Token Bucket.
		String tokenKey = prefix + "." + REDIS_PROPERTY_TOKENS;
		String timestampKey = prefix + "." + REDIS_PROPERTY_TIMESTAMP;

		List tokens = Arrays.asList(tokenKey, timestampKey);

		try {
			int replenishRate = (int)context.getReplenishRate();
			int burstCapacity = (int)context.getBurstCapacity();
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

			return RateLimitHandleResponse.of(BurstableRateLimitState.builder().tokens(redisResult.get(1)).build().refresh());
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
	protected boolean isValid(Optional<BurstableRateLimitState> burstableRateLimitState) {
		return Objects.nonNull(burstableRateLimitState) && burstableRateLimitState.isPresent();
	}

	@Override
	protected BurstableRateLimitState createRateLimitState(CONTEXT context) {

		BurstableRateLimitState state = BurstableRateLimitState.builder().build();
		return state;
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