# RateLimiter Springboot 


This library will require that you have a `RedisTemplate`, `RedisScript` beans configured and ready to be used for
transactional support.

Please note that this implementation, specifically the lua script, was largely lifted from the springboot gateway 
ratelimiter so kudos to those developers for really making this happen.
[Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

You should probably be using their implementation and the Gateway project to filter external requests as they already
have support for request filter chains, headers, etc. This project was mainly created to serve a rate limiting purpose
with lambda operations and external requests made by my own project/service.

## Implementation

First add the project to your springboot application

#### Pom.xml
```xml
<dependency>
    <groupId>com.calebjonasson.ratelimiter</groupId>
    <artifactId>ratelimiter-springboot</artifactId>
</dependency>
```


Then choose between autoconfiguration and a custom configuration.

### Via Autoconfiguration:

Simply add the following annotation to your spring application file or better yet a configuration file that contains your
autoconfigurations.


#### Springboot application file.
```java

import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRedisRateLimiter
@SpringBootApplication
class YourProjectApplication {
	// ...
}
```

### Via Custom Configuration:

This is pretty standard and the implementation details can be found using the API. Basically, you can use this
configuration as an example:


`com.calebjonasson.ratelimiter.springboot.configuration.RedisRateLimiterConfiguration`

```java
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
```
Alternatively you can use the in memory rate limiter configuration which is part of the core
`com.calebjonasson.ratelimiter` library.