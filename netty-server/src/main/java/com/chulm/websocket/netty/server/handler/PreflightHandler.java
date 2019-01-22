package com.chulm.websocket.netty.server.handler;


//https://developer.mozilla.org/ko/docs/Web/HTTP/Access_control_CORS

import com.chulm.websocket.netty.server.utils.AttributeKeys;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.IllegalReferenceCountException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * CORS관련 사전 요청 처리
 */

@Slf4j
public class PreflightHandler extends SimpleChannelInboundHandler {

    public PreflightHandler() {
        super(false);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return (msg instanceof HttpRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        SocketAddress remoteAddress = ctx.pipeline().channel().remoteAddress();
        InetSocketAddress remote = (InetSocketAddress) remoteAddress;
        log.info("PreflightHandler(" + remote + ") channelRead0()");

        HttpRequest request = (HttpRequest) msg;

        String origin = request.headers().get(HttpHeaderNames.ORIGIN);

        if (origin != null) {
            log.info("PreflightHandler(" + remote + ") channelRead0() origin:" + origin);
            ctx.channel().attr(AttributeKeys.origin).set(origin);
        }

        String corsHeaders =request.headers().get(HttpHeaderNames.ACCESS_CONTROL_REQUEST_HEADERS);

        if (corsHeaders != null) {
            log.info("PreflightHandler(" + remote + ") channelRead0() corsHeaders: " + corsHeaders);
            ctx.channel().attr(AttributeKeys.corsHeaders).set(corsHeaders);
        }

        if (request.method().equals(HttpMethod.OPTIONS)) {
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
            response.headers().set(HttpHeaderNames.CACHE_CONTROL, "max-age=31536000, public");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "31536000");

            if (request.uri().equalsIgnoreCase("/xhr")) {
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "OPTIONS, POST");
            } else {
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "OPTIONS, GET");
            }
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS , "Content-Type");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS , "true");

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocketAddress remoteAddress = ctx.pipeline().channel().remoteAddress();
        InetSocketAddress remote = (InetSocketAddress) remoteAddress;

        if (!ctx.pipeline().names().contains("write-timeout")) {
            log.debug("PreflightHandler(" + remote + ") time out");
            return;
        }

        log.debug("PreflightHandler(" + remote + ") exceptionCaught()" + cause.toString());
        if (cause instanceof IllegalReferenceCountException) {
            log.debug("Unexpected release (need a safety release) has occured in [" + this.getClass().getSimpleName() + "]");
        } else {
            log.debug("Unexpected error [" + cause.getMessage() + "] has occured in [" + this.getClass().getSimpleName() + "]");
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }
}