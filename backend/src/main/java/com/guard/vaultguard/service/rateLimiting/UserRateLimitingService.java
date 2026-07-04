package com.guard.vaultguard.service.rateLimiting;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserRateLimitingService extends AbstractRateLimiting {

    @Value("${app.rate-limit.user.capacity}")
    private int USER_TOKEN_CAPACITY;

    @Value("${app.rate-limit.user.refill-period}")
    private int USER_TOKEN_REFILL_INTERVAL;

    @Value("${app.rate-limit.user.refill-tokens}")
    private int USER_REFILL_TOKENS;

    protected UserRateLimitingService(ProxyManager<String> proxyManager) {
        super(proxyManager);
    }

    public Bucket resolveUserBucket(String userId) {
        return super.resolveBucket(
                "v1.4_USER_", userId, USER_TOKEN_CAPACITY, USER_TOKEN_REFILL_INTERVAL, USER_REFILL_TOKENS
        );
    }
}
