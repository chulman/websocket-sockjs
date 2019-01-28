package com.chulm.websocket.spring.server.reactive.handler;

import reactor.core.publisher.UnicastProcessor;

import java.util.Optional;

public  class  WebSocketMessageSubscriber {
    private UnicastProcessor<Object> messagePublisher;
    private Optional<Object> lastReceivedMessage = Optional.empty();

    public WebSocketMessageSubscriber(UnicastProcessor<Object> messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public void onNext(String message) {
        System.err.println("message :" + message );
        lastReceivedMessage = Optional.of(message);
        messagePublisher.onNext(message);
    }

    public void onError(Throwable error) {
        error.printStackTrace();
    }

    public void onComplete() {
        System.err.println("onComplete" );
        lastReceivedMessage.ifPresent(messagePublisher::onNext);
    }
}