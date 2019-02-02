package com.chulm.websocket.spring.server.reactive.controller;


import com.chulm.websocket.spring.server.domain.repository.MessageCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;
import rx.Observable;


@RestController
public class MessageController {

    @Autowired
    private MessageCacheRepository messageCacheRepository;

    @GetMapping("/api/{id}")
    public Mono<Object> helloMono(@PathVariable String id) {
        return Mono.just(toDeferredResult(messageCacheRepository.get(id)));
    }


    private static <T> DeferredResult<T> toDeferredResult(Observable<Object> observable) {
        DeferredResult<T> deferredResult = new DeferredResult<>();
        observable.subscribe(deferredResult::setErrorResult);
        return deferredResult;
    }

}