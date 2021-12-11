# Ratelimiter

This project was created from a personal project that needed a way to limit internal lambda like operations and external
requests. Some of the code was lifted from the Spring Cloud Gateway project. It's a great project; you can learn more
about it here: [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

The mechanism for rate limiting via redis is done via lua script and is based on the Stripe burstable rate limit which
is very well talked about and implemented here: [Scaling your API with rate limiters](https://stripe.com/blog/rate-limiters)




### Ratelimiter Strategies

#### Atomic
Atomic rate limiters act in a block that gets reset after a given interval of time.

Configuration properties:
- **limit** The amount of ratelimits to occur during a given interval.
- **interval** The period between limit resets in milliseconds.

#### Burstable
The Burstable ratelimiter will dynamically refill its capacity according to a refresh rate.

Configuration properties:
- **replenishRate** The amount of capacity that is refilled per second.
- **burstCapacity** The capacity that is allowed.

## Implementation

### InMemory Ratelimiter - Burstable

```java

class Application {
	
	public static void main(String[] args) {
		String contextKey = "user-auth:asdf634hj2g3hj5ghj23g5";
		String stateKey = "api:get:/users";

		long replenishRate = 1; // The amount of times the capacity is dynamically refilled every second.
		long burstCapacity = 5; // The amound of total burst capacity.

		// A configuration for the context creation policy
		BurstableContextConfiguration configuration = ContextConfigurations.burstableConfiguration(replenishRate, burstCapacity);

		// A mechanism that will create the context from the passed in configuration.
		// In this scenario the context is stored in a HashMap.
		BurstableInMemoryContextProvider contextProvider = new BurstableInMemoryContextProvider(configuration);

		// The rate limiter that stores the state of the rate limiter in a HashMap.
		BurstableInMemoryRateLimiter rateLimiter = new BurstableInMemoryRateLimiter(contextProvider, configuration);

		// Call the rate limiter handle method. Throws a RateLimitExceededException
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
	}
}

```

### InMemory Ratelimiter - Atomic

```java

class Application {
	
	public static void main(String[] args) {
		String contextKey = "user-auth:asdf634hj2g3hj5ghj23g5";
		String stateKey = "api:get:/users";

		long limit = 3; // The amount of rate limits that can occur during an interval.
		long interval = 1000; // The interval in milliseconds that the limit is reset.

		AtomicInMemoryContextProvider contextProvider = ContextProviderFactory.atomicInMemoryContextProvider(this.atomicContextConfiguration);
		contextProvider.putContext(DEFAULT_CONTEXT_KEY, AtomicRateLimitContext.builder().limit(limit).interval(interval).build());
		InMemoryRateLimiter limiter = RateLimiterFactory.inMemoryRateLimiter(contextProvider);

		// Call the rate limiter handle method. Throws a RateLimitExceededException
		rateLimiter.handle(ContextStateRateLimitRequest.of(contextKey, stateKey));
	}
}

```


## Subprojects

### Core
The core module contains the interfaces used by the implementations. There are no concrete usages here.

### InMemory
The in memory project contains implementations for context storage as well as state storage.

### Springboot
The springboot project contains implementations for springboot and redis with pre built configurations.

### Test
This is a project that is purely used during the testing of rate limiters.


## Future plans
* Split out the spring implementation to be ratelimiter-spring and ratelimiter-spring-redis respectively.
* Add a service implementation that will allow for turn key operations and resizing of the memory footprint.
* Add testing harnesses to support the testing of many rate limiter strategies.
* Add a jdbc context provider
* Add a burstable replenish unit to support a replenish rate of milliseconds, minutes, hours