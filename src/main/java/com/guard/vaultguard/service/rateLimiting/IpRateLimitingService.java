package com.guard.vaultguard.service.rateLimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This is layer 1 of rate limit, thsi handels the IP address rate limit
 */
@Service
public class IpRateLimitingService extends AbstractRateLimiting{

    @Value("${app.rate-limit.ip.capacity}")
    private int REQUEST_CAPACITY; // Maximum requests per second

    @Value("${app.rate-limit.ip.refill-period}")
    private long REFILL_INTERVALS; // based in ms

    @Value("${app.rate-limit.ip.refill-tokens}")
    private int REFILL_TOKENS; // tokens refilled per REFILL_INTERVALS

    public IpRateLimitingService(ProxyManager<String> proxyManager){
        super(proxyManager);
    }

    public Bucket resolveBucket(String clientIp){
        return super.resolveBucket(
                "v1.4_IP_", clientIp, REQUEST_CAPACITY, REFILL_INTERVALS, REFILL_TOKENS
        );
    }
}
