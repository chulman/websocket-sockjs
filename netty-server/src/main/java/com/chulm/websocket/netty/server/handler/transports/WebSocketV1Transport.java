package com.chulm.websocket.netty.server.transports;

import com.chulm.websocket.netty.server.frame.Frame;
import com.chulm.websocket.netty.server.utils.SockJsResponse;
import com.chulm.websocket.netty.server.utils.UrlUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Data
@Slf4j
public class WebSocketV1Transport implements SockJsTransport{

    @Override
    public void doOpen(ChannelHandlerContext ctx, HttpRequest req, String webSockLocation, String path) {

        log.debug("request path={}", path);
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
            SockJsResponse.HandShakeAndSendResponse(ctx, req, webSockLocation);

            System.err.println("------> writes" );

        } else {
            SockJsResponse.send404NotFoundResponse(ctx, req);
        }
    }

    @Override
    public void doClose(ChannelHandlerContext ctx) {
        log.info("channel close :"  + ctx.channel().toString());
        ctx.channel().close();
    }

    @Override
    public void onError(ChannelHandlerContext ctx, Throwable throwable) {
        log.info("occured onError.(" + ctx.channel().toString() + ")");
        doClose(ctx);
    }

    @Override
    public void onBinary(ChannelHandlerContext ctx, WebSocketFrame frame) {
        log.info("BinaryFrame: " + frame) ;
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, WebSocketFrame frame) {

        log.info("TextFrame: " + frame) ;

        /**
         * Echo - 받은 메세지 그대로 응답.
         */
        ctx.channel().writeAndFlush(frame).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        });
    }


    @Override
    public void onEvent(ChannelHandlerContext ctx, Object evt) {
        log.info("occured event.(" + ctx.channel().toString() + ")");
    }
}
