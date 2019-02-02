package com.chulm.websocket.spring.server.reactive.client;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Data
@Builder
public class ReactiveNettyBaseClient {

    private String uri;
    private String payload;

    private final WebSocketClient client = new ReactorNettyWebSocketClient();

    public void send(){
        client.execute(
                URI.create(uri),
                session -> session.send(
                        Mono.just(session.textMessage(payload)))
                        .thenMany(session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .log())
                        .then())
                .block(Duration.ofSeconds(60*10l));
    }


}
