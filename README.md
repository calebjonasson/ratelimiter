# Ratelimiter

This project was created from a personal project that needed a way to limit internal lambda like operations and external
requests. Some of the code was lifted from the Spring Cloud Gateway project. It's a great project; you can learn more
about it here: [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

The mechanism for rate limiting via redis is done via lua script and is based on the Stripe burstable rate limit which
is very well talked about and implemented here: [Scaling your API with rate limiters](https://stripe.com/blog/rate-limiters)

## Implementing

The implementation documentation can be found within the sub-projects. `./ratelimiter-core` and `./ratelimiter-springboot`

The Springboot rate limiter implementation contains a redis rate limiter and will require that you use `RedisTemplate` and
`RedisScript` implementations.