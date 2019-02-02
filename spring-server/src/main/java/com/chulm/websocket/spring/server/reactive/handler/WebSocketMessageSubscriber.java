package com.chulm.websocket.spring.server.reactive.handler;

import com.chulm.websocket.spring.server.domain.repository.MessageCacheRepository;
import reactor.core.publisher.UnicastProcessor;

import java.util.Optional;


public class WebSocketMessageSubscriber {


    private final String id = "test";

    private UnicastProcessor<Object> messagePublisher;
    private Optional<Object> lastReceivedMessage = Optional.empty();

    private MessageCacheRepository messageCacheRepository;

    public WebSocketMessageSubscriber(UnicastProcessor<Object> messagePublisher, MessageCacheRepository messageCacheRepository) {
        this.messagePublisher = messagePublisher;
        this.messageCacheRepository = messageCacheRepository;

    }

    public void onNext(String message) {
        System.err.println("onNext() message :" + message);
        lastReceivedMessage = Optional.of(message);
        messagePublisher.onNext(message);

        messageCacheRepository.set(id, message);
    }

    public void onError(Throwable error) {
        error.printStackTrace();
    }

    public void onComplete() {
        System.err.println("onComplete");
        lastReceivedMessage.ifPresent(messagePublisher::onNext);
    }
}