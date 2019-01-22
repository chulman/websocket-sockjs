package com.chulm.websocket.netty.server.handler.session;

import lombok.Data;


@Data
public class Session {

   private String address;
   private String sessionId;

   private DataListener dataListener;

    public Session(String address, String sessionId) {
        this.address = address;
        this.sessionId = sessionId;
    }
}
