package com.calebjonasson.ratelimiter.springboot.configuration;

import com.calebjonasson.ratelimiter.core.context.ContextProvider;
import com.calebjonasson.ratelimiter.springboot.context.RedisContextProvider;
import com.calebjonasson.ratelimiter.springboot.limiter.RedisRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

// Only perform these operations if we have a redis template.
//@ConditionalOnBean(ReactiveStringRedisTemplate.class)
@Configuration
public class RedisRateLimiterConfiguration {

	@Autowired protected ReactiveStringRedisTemplate redisTemplate;

	@Bean
	@SuppressWarnings("unchecked")
	public RedisScript redisRequestRateLimiterScript() {
		DefaultRedisScript redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
		redisScript.setResultType(List.class);
		return redisScript;
	}

	@Bean
	public ContextProvider redisContextProvider() {
		RedisContextProvider contextProvider = new RedisContextProvider(this.redisTemplate);
		return contextProvider;
	}

	@Bean
	public RedisRateLimiter redisRateLimiter() {
		RedisRateLimiter rateLimiter = new RedisRateLimiter(
				this.redisContextProvider(),
				this.redisTemplate,
				this.redisRequestRateLimiterScript());
		return rateLimiter;
	}

}