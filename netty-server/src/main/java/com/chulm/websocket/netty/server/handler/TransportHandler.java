package com.chulm.websocket.netty.server.handler;

import com.chulm.websocket.netty.server.handler.transports.SockJsTransport;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TransportHandler extends ChannelInboundHandlerAdapter {

    private final SockJsTransport sockJsTransport;

    public TransportHandler(SockJsTransport sockJsTransport) {
        this.sockJsTransport = sockJsTransport;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("{} channel read()", getClass().getName());

        if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, ((WebSocketFrame) msg).retain());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        sockJsTransport.onError(ctx, cause);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        sockJsTransport.onEvent(ctx, evt);
    }


    private void handleWebSocketFrame(ChannelHandlerContext ctx, Object frame) {


        if (!(frame instanceof WebSocketFrame)) {
            log.info("Frame is not WebSocketFrame" + ctx.channel());
        }

        WebSocketFrame wFrame = (WebSocketFrame) frame;

        if (wFrame instanceof TextWebSocketFrame) {
            sockJsTransport.onMessage(ctx, wFrame.retain());
        }
        if (wFrame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(wFrame.content().retain()));
        }
        if (wFrame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(wFrame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
        }
        if (wFrame instanceof BinaryWebSocketFrame) {
            sockJsTransport.onBinary(ctx, wFrame.retain());
        }
        if (wFrame instanceof PongWebSocketFrame) {

        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        sockJsTransport.doClose(ctx);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sockJsTransport.doClose(ctx);
        super.channelInactive(ctx);
    }
}