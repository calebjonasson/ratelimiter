package com.calebjonasson.ratelimiter.core.type.strategy;

/**
 * This strategy is used as a type and a glue between context providers and their factories to create contexts of the
 * same type. Contexts are created from context configurations. These context configurations need the correct typings
 * to know that they can create a given context.
 *
 */
public interface RateLimiterTypeStrategy {

}