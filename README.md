# Ratelimiter

This project was created from a personal project that needed a way to limit internal lambda like operations and external
requests. Some of the code was lifted from the Spring Cloud Gateway project. It's a great project; you can learn more
about it here: [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

The mechanism for rate limiting via redis is done via lua script and is based on the Stripe burstable rate limit which
is very well talked about and implemented here: [Scaling your API with rate limiters](https://stripe.com/blog/rate-limiters)

## Implementing

The implementation documentation can be found within the sub-projects. `./core` and `./springboot`

The Springboot rate limiter implementation contains a redis rate limiter and will require that you use `RedisTemplate` and
`RedisScript` implementations.

### Inmemory Ratelimiter Sample:

```java
class Application {

	public static void main(String args[]) {
		String contextKey = "user-auth:asdf634hj2g3hj5ghj23g5";
		String stateKey = "api:get:/users";

		InMemoryRateLimiter rl = InMemoryRateLimiter.of(ContextProviderFactory.inMemoryContextProvider());

		rl.atomic(contextKey, stateKey);
	}	
}
```

## Future plans
* Split out the spring implementation to be ratelimiter-spring and ratelimiter-spring-redis respectively.
* Split out the rate limiting strategies eg: BurstableRateLimiterStrategy vs AtomicRateLimiterStrategy
* Add a service implementation that will allow for turn key operations and resizing of the memory footprint.
* Add testing harnesses to test many different rate limiter styles.
* Add a jdbc context provider