package com.chulm.websocket.netty.server.frame;

public class SockJsMessage {
    private String message;

    public SockJsMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SockJsMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}