package com.chulm.websocket.netty.server.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.util.Random;

public class SockJsResponse {

    public static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        if (res.status().code() != 200) {
            res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            ctx.channel().write(res).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.channel().write(res);
        }
    }

    public static void send404NotFoundResponse(ChannelHandlerContext ctx, HttpRequest request) {
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        ByteBuf message = Unpooled.copiedBuffer(HttpResponseStatus.NOT_FOUND.codeAsText(), CharsetUtil.UTF_8);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, message.readableBytes());

        boolean hasKeepAliveHeader = HttpHeaderValues.KEEP_ALIVE.toString().equalsIgnoreCase(request.headers().get(HttpHeaderNames.CONNECTION));
        if (hasKeepAliveHeader) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        String origin = request.headers().get("Origin");
        response.headers().set("Access-Control-Allow-Origin", origin == null || "null".equals(origin) ? "*" : origin);
        response.headers().set("Access-Control-Allow-Credentials", "true");
        String corsHeaders = request.headers().get("Access-Control-Request-Headers");
        if (corsHeaders != null) {
            response.headers().set("Access-Control-Allow-Headers", corsHeaders);
        }

        ctx.channel().writeAndFlush(response);
    }


    public static ChannelFuture HandShakeAndSendResponse(ChannelHandlerContext ctx, HttpRequest request, String wsLocation) {

        // Compatibility hack for Firefox 6.x
        String connectionHeader = request.headers().get(HttpHeaderNames.CONNECTION);
        if (connectionHeader != null && connectionHeader.equals("keep-alive, Upgrade")) {
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
        }

        String wsVersionHeader = request.headers().get(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
        if (wsVersionHeader != null && wsVersionHeader.equals("7")) {
            request.headers().set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, "13");
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsLocation, null, false);

        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);

        if (handshaker == null) {
//            wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return null;
        } else {
            return handshaker.handshake(ctx.channel(), request);
        }
    }


    public static ByteBuf getInfoResponse() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("{");
        sb.append("\"websocket\": ");
        //        sb.append(metadata.isWebSocketEnabled());
        sb.append(true);
        sb.append(", ");
        sb.append("\"origins\": [\"*:*\"], ");
        sb.append("\"cookie_needed\": ");
        //        sb.append(metadata.isCookieNeeded());
        sb.append(false);
        sb.append(", ");
        sb.append("\"entropy\": ");
        sb.append(new Random().nextInt(Integer.MAX_VALUE) + 1);
        sb.append("}");
        //        return Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
        return Unpooled.copiedBuffer("{\"websocket\":true,\"origins\":[\"*:*\"],\"cookie_needed\":false,\"entropy\":3496992730}", CharsetUtil.UTF_8);
    }

}
