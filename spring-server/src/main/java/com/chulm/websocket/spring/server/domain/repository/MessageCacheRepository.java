package com.chulm.websocket.spring.server.domain.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.netflix.hystrix.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rx.Observable;


@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MessageCacheRepository {

    @Value("${spring.redis.default.expire-time-seconds}")
    private long expireTimeSeconds;


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private StatefulRedisConnection<String, String> redisConnection;


    public Observable<Object> get(String id) {
        return new MessageCacheGetCommand(redisConnection, id).toObservable();
    }

    public void set(String id, String value) {
        if (HystrixCircuitBreaker.Factory.getInstance(HystrixCommandKey.Factory.asKey(MessageCacheGetCommand.class.getSimpleName())).allowRequest()) {
            redisConnection.sync().setex(id, expireTimeSeconds, value);
            log.info("Cache update: {} => {}", id, value);
        } else {
            log.info("Cache update canceled, Circuit breaker opened!");
        }
    }

    static class MessageCacheGetCommand extends HystrixObservableCommand<Object> {

        @Autowired
        private final StatefulRedisConnection<String, String> redisConnection;
        private String id;

        private static Setter setter = Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(MessageCacheGetCommand.class.getSimpleName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(MessageCacheGetCommand.class.getSimpleName()))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)  // semaphore strategy
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(5) // failure 5 requsts, circuit on.
                                .withExecutionTimeoutEnabled(true)
                                .withExecutionTimeoutInMilliseconds(100)
                );

        MessageCacheGetCommand(StatefulRedisConnection<String, String> redisConnection, String id) {
            super(setter);
            this.redisConnection = redisConnection;
            this.id = id;
        }

        @Override
        protected Observable<Object> construct() {
            return redisConnection.reactive()
                    .get(id)
                    .defaultIfEmpty(null)
                    .map(value -> {
                        log.info("Cache {}: {} => {}", (value != null) ? "hit" : "miss", id, value);
                        return value;
                    });
        }

        // fallback is empty data.
        @Override
        protected Observable<Object> resumeWithFallback() {
            return Observable.empty();
        }
    }
}
