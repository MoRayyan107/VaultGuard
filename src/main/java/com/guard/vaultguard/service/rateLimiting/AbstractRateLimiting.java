package com.guard.vaultguard.service.rateLimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;

public abstract class AbstractRateLimiting {

    private final ProxyManager<String> proxyManager;

    protected AbstractRateLimiting(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    /**
     * Resolves a bucket for rate limiting based on the provided parameters.
     * @param prefixKey to add a key with value either IP or User ("v1_IP" or "user_")
     * @param key the Ip address or username/ID form that user
     * @param capacity capacity of tokens in a bucket
     * @param refillIntervals time to refill tokens in milliseconds
     * @param refillTokens number of tokens to refill in that bucket
     * @return a {@code Bucket} instance for rate limiting
     */
    protected Bucket resolveBucket(String prefixKey, String key, int capacity, long refillIntervals, int refillTokens) {
        String storageKey = prefixKey + key;
        return proxyManager.builder()
                .build(key, () -> bucketConfig(capacity, refillIntervals, refillTokens));
    }

    /**
     * Defines the configuration for rate limiting for a specific bucket.
     * @param capacity capacity of tokens in a bucket
     * @param refillIntervals time to refill tokens in milliseconds
     * @param refillTokens number of tokens to refill in that bucket
     * @return {@code BucketConfiguration} instance for the specified parameters
     */
    protected BucketConfiguration bucketConfig(int capacity, long refillIntervals, int refillTokens) {
        var limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(refillTokens, java.time.Duration.ofMillis(refillIntervals))
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }


}
