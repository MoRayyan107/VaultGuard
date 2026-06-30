package com.guard.vaultguard.service.rateLimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * This is layer 1 of rate limit, thsi handels the IP address rate limit
 */
@Service
public class IpRateLimitingService {

    @Value("${app.rate-limit.ip.capacity}")
    private int REQUEST_PER_SECOND; // Maximum requests per second

    @Value("${app.rate-limit.ip.refill-period}")
    private long REFILL_INTERVALS; // based in ms

    @Value("${app.rate-limit.ip.refill-tokens}")
    private int REFILL_TOKENS; // tokens refilled per REFILL_INTERVALS

    // storage bucket (Ip/User Id -> Bucket)
    private final ProxyManager<String> proxyManager;

    public IpRateLimitingService(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    // key -> on what key basis are we usng rate limiting, is it user Id or its IP address,
    // mostly we gonna use Client ID as key for rate limit.
    public Bucket resolveBucket(String clientIp) {
        Supplier<BucketConfiguration> configurationSupplier = this::bucketConfig;

        return proxyManager.builder()
                .build(clientIp, configurationSupplier);
    }

    /**
     * Defines the configuration for rate limiting for a specific IP address
     * @return
     */
    private BucketConfiguration bucketConfig() {
        // How many tokens a bucket can hold
        // how quilkly tokens are refilled

        var limit = Bandwidth.builder()
                .capacity(REQUEST_PER_SECOND)
                .refillIntervally(REFILL_TOKENS, Duration.ofMillis(REFILL_INTERVALS))
                .build();

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }
}
