package com.guard.vaultguard.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public RedisClient redisClient(RedisConnectionDetails redisConnectionDetails) {
        String redisHost = redisConnectionDetails.getStandalone().getHost();
        int redisPort = redisConnectionDetails.getStandalone().getPort();

        return RedisClient.create(
                RedisURI.builder()
                        .withHost(redisHost)
                        .withPort(redisPort)
                        .build()
        );
    }

    @Bean
    public ProxyManager<?> proxyManager(RedisClient redisClient) {
        // first we need a redis connection
        var redisConnection = redisClient.connect(
                RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)
        );

        // handel expiration TTL
        var expirationStrategy  = ExpirationAfterWriteStrategy
                .basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(30));

        // set the client side config with expiration strategy Not needed since the newer version doesnt require it)
//        var clientConfig = ClientSideConfig.getDefault()
//                .withExpirationAfterWriteStrategy(expirationStrategy );

        // Build the proxy Manager
        return Bucket4jLettuce.casBasedBuilder(redisConnection)
                .expirationAfterWrite(expirationStrategy)
                .build();
    }
}
