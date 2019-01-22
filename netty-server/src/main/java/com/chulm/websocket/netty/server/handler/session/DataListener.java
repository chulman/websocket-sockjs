package com.chulm.websocket.netty.server.handler.session;


public interface DataListener {
    public void onData(String message);
}