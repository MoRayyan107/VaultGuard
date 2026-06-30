package com.guard.vaultguard.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisClient redisClient() {
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
