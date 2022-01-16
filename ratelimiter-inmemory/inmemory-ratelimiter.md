# In Memory RateLimiter

The in memory rate limiter will allow for really simple implementations that can be implemented with pre defined factory
methods.

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