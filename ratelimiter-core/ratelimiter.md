# Rate Limiter


A rate limiter should be able to support the general concept of having many rates sharded by key.

## Rate Limiter Pattern

The rate limiter pattern needs to use a key for matching, an atomic value, as well as an expiration
date to store the state of the rate limiting operation. This information should be stored in redis.

Configurations for the rate limiter pattern should likely be stored in a datasource that is not as volatile
We will store these values in our implementation via mongodb. The configuration values will be pulled
and cached in redis to make the operations quick.




## Principals
* Shard by key. crawl.request.url.domain::reddit.com
* Configure by shard matching.
* Turn on and off rate limiter by configuration.
* Adapt out the storage mechanism: elasticache/redis.
* Configure default values for time
* Configure rate limits.


## Models

* **RateLimitState** - the structure to be stored in redis.
* **RateLimit** - the rules for the structure storage.


# Patterns


```
interface: RateLimiter
- ContextProvider: allows the storage and access of contexts.
- StateProvider: allows the storage and access of states.


interface: RateLimiterType Atomic/Burstable

interface: RateLimiterTypeStrategy
- 

 
```