package com.chulm.websocket.spring.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketMessage;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class EchoMessage  implements WebSocketMessage {
    private String message;
    private LocalDateTime time;

    @Override
    public Object getPayload() {
        return null;
    }

    @Override
    public int getPayloadLength() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public String toString() {
        return "EchoMessage{" +
                "message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}