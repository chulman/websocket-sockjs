package com.chulm.websocket.netty.server.handler.transports;

import com.chulm.websocket.netty.server.frame.Frame;
import com.chulm.websocket.netty.server.frame.SockJsMessage;
import com.chulm.websocket.netty.server.handler.session.Session;
import com.chulm.websocket.netty.server.handler.session.SessionManager;
import com.chulm.websocket.netty.server.utils.SockJsResponse;
import com.chulm.websocket.netty.server.utils.UrlUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Data
@Slf4j
public class WebSocketV1Transport implements SockJsTransport {

    private final SessionManager sessionManager = SessionManager.getInstance();
    @Override
    public void doOpen(ChannelHandlerContext ctx, HttpRequest req, String webSockLocation, String path) {

        //path checker
        /**
         *  /ws/serverid/sessionid/transport..
         */

        String[] requestPath = path.split("/");

        String startPath = requestPath[0];
        String serverId = requestPath[1];
        String sessionId = requestPath[2];
        String transport = requestPath[3];

        if (transport.equals(UrlUtils.WEBSOCKET)) {

            ChannelFuture future = SockJsResponse.HandShakeAndSendResponse(ctx, req, webSockLocation);

            if (future == null) {
                doClose(ctx);
            } else {
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {

                        Session session = new Session(future.channel().pipeline().channel().remoteAddress().toString(),sessionId);
                        session.setDataListener(message -> {
                            System.err.println(message);
                        });
                        sessionManager.put(future.channel().pipeline().channel().remoteAddress().toString(),session);
                        log.info("Websocket Connected User Count : <{}>", sessionManager.getUserCount());

                        // add SessionHandler
                        // preflight , routing handler remove
                        future.channel().pipeline().remove("preflightHandler");
                        future.channel().pipeline().remove("routerHandler");

                    }
                });

                //write open
                ByteBuf open = Unpooled.directBuffer(Frame.OPEN_FRAME.length);
                open.writeBytes(Frame.OPEN_FRAME);
                textFrameWrites(ctx,open);

                //write heartbeat
                ByteBuf heartBeat = Unpooled.directBuffer(Frame.HEARTBEAT_FRAME.length);
                heartBeat.writeBytes(Frame.HEARTBEAT_FRAME);
                textFrameWrites(ctx,heartBeat);
            }

        } else {
            SockJsResponse.send404NotFoundResponse(ctx, req);
            doClose(ctx);
        }
    }

    @Override
    public void doClose(ChannelHandlerContext ctx) {
        log.info("channel close :({})", ctx.channel().toString());
        sessionManager.remove(ctx.channel().remoteAddress().toString());
        log.info("Websocket Connected User Count : <{}>", sessionManager.getUserCount());
        ctx.channel().close();
    }

    @Override
    public void onError(ChannelHandlerContext ctx, Throwable throwable) {
        log.info("occured onError.({}), <{}>", ctx.channel(), throwable);
        doClose(ctx);
    }

    @Override
    public void onBinary(ChannelHandlerContext ctx, WebSocketFrame frame) {
        log.info("BinaryFrame: {}",frame);
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, WebSocketFrame frame) {

        byte[] array;

        if(frame.content().hasArray()){
            array = frame.content().array();
        }else{
            array = new byte[frame.content().readableBytes()];
            frame.content().getBytes(frame.content().readerIndex(), array);
        }

        String str = new String(array);

        if(str.contains("[") || str.contains("]")){
            str = str.replace("[","").replace("]","");
        }

        SockJsMessage readMsg = Frame.messageParse(str);

        log.info("{} onMessage = {}", getClass().getName(), str);
        //frame is direct buffer
        TextWebSocketFrame echo = new TextWebSocketFrame(Frame.messageFrame(readMsg).getData());


        /**
         * Echo - 받은 메세지 그대로 응답.
         */
        ctx.channel().writeAndFlush(echo);

    }


    @Override
    public void onEvent(ChannelHandlerContext ctx, Object evt) {
        log.info("occured onEvent.({}), <{}>", ctx.channel(), evt);
    }

    private boolean addHandlerIfNotExists(ChannelPipeline pipeline, String name, ChannelHandlerAdapter handler) {
        if (pipeline.get(name) == null) {
            pipeline.addLast(name, handler);
            return true;
        } else {
            return false;
        }
    }

    private void textFrameWrites(ChannelHandlerContext ctx, ByteBuf buf){
        TextWebSocketFrame tf = new TextWebSocketFrame(buf);

        // send openFrame, send heartbeatFrame
        ctx.channel().pipeline().writeAndFlush(tf).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    future.cause().printStackTrace();
                }
            }
        });
    }
}
