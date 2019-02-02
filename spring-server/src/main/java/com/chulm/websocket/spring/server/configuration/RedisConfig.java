package com.chulm.websocket.spring.server.configuration;


import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.codec.StringCodec;
import com.lambdaworks.redis.resource.ClientResources;
import com.lambdaworks.redis.resource.DefaultClientResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.lettuce.io.pool-size}")
    private int ioPoolSize;

    @Bean
    public StatefulRedisConnection<String, String> lettuceConnection() {
        RedisURI redisURI = RedisURI.create(host, port);
        return RedisClient.create(clientResources(), redisURI).connect(StringCodec.UTF8);
    }

    private ClientResources clientResources() {
        return DefaultClientResources.builder()
                .ioThreadPoolSize(ioPoolSize)
                .build();
    }
}