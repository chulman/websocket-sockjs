package com.chulm.websocket.netty.server.handler.transports;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface SockJsTransport {
    public void doOpen(ChannelHandlerContext ctx, HttpRequest req, String webSockLocation, String path);
    public void doClose(ChannelHandlerContext ctx);

    public void onError(ChannelHandlerContext ctx, Throwable throwable);
    public void onBinary(ChannelHandlerContext ctx, WebSocketFrame frame);
    public void onMessage(ChannelHandlerContext ctx, WebSocketFrame frame);
    public void onEvent(ChannelHandlerContext ctx, Object evt);


}
