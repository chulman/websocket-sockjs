package com.chulm.websocket.spring.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class MessageHandleController {

    private static final String prefix = "[echo server] : ";

    @MessageMapping("/hello")
    @SendTo("/topic/echo")
    public EchoMessage greeting(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new EchoMessage(LocalDateTime.now(),prefix + message);
    }
}

class EchoMessage{
    private LocalDateTime time;
    private String messge;

    public EchoMessage(LocalDateTime time, String messge) {
        this.time = time;
        this.messge = messge;
    }
}
