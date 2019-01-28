package com.chulm.websocket.test;

import com.chulm.websocket.spring.server.domain.EchoMessage;
import com.chulm.websocket.spring.server.reactive.client.ReactiveNettyBaseClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.LocalDateTime;

public class WebfluxClientTest {

    @Test
    public void send() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        EchoMessage message = new EchoMessage("hello_world", LocalDateTime.now());

        String uri = "ws://localhost:8080/reactive/websocket";
        String payload= mapper.writeValueAsString(message);

        ReactiveNettyBaseClient client = ReactiveNettyBaseClient.builder()
                                                                .uri(uri)
                                                                .payload(payload).build();
        client.send();
    }
}
