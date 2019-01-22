package com.chulm.websocket.netty.server.handler;

import com.chulm.websocket.netty.server.handler.transports.SockJsTransport;
import com.chulm.websocket.netty.server.utils.SockJsResponse;
import com.chulm.websocket.netty.server.utils.UrlUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Calendar;


@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private String wsPath;
    private boolean isHttps;
    private final SockJsTransport sockJsTransport;

    public WebSocketServerHandler(String wsPath, boolean isHttps, SockJsTransport sockJsTransport) {
        this.wsPath = wsPath;
        this.isHttps = isHttps;
        this.sockJsTransport = sockJsTransport;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("{} read message=<{}>",getClass().getName(),msg);
        if(msg instanceof HttpRequest){
            handleHttpPath(ctx, msg);
        }

        if(msg instanceof  WebSocketFrame){
            handleWebSocketFrame(ctx, msg);
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


    private void handleHttpPath(ChannelHandlerContext ctx, Object msg){
        SocketAddress remoteAddress = ctx.pipeline().channel().remoteAddress();
        InetSocketAddress remote = (InetSocketAddress) remoteAddress;
        HttpRequest request = (HttpRequest) msg;
        String uri = request.uri();

        request.setUri(uri.replaceFirst(wsPath, ""));
        QueryStringDecoder qsd = new QueryStringDecoder(request.uri());
        String path = qsd.path();

        log.debug("WebSocketServerHandler(" + remote + ") handleService() path:" + path);

        if (path.startsWith(UrlUtils.START_WITH_INFO_PATH)) {
            sendInfoResponse(ctx, request);
        }else{
            handleWebSocketPath(ctx, path, msg);
        }
    }

    private void sendInfoResponse(ChannelHandlerContext ctx, HttpRequest request){
        ByteBuf message = SockJsResponse.getInfoResponse();
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, message);
        String origin = request.headers().get(HttpHeaderNames.ORIGIN);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin == null || "null".equals(origin) ? "*" : origin);
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

        String corsHeaders = request.headers().get(HttpHeaderNames.ACCESS_CONTROL_REQUEST_HEADERS);
        if (corsHeaders != null) {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, corsHeaders);
        }
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-store, no-cache, no-transform, must-revalidate, max-age=0");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");

        boolean hasKeepAliveHeader = HttpHeaderValues.KEEP_ALIVE.toString().equalsIgnoreCase(request.headers().get(HttpHeaderNames.CONNECTION));

        if (hasKeepAliveHeader) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
            response.headers().set(HttpHeaderValues.KEEP_ALIVE.toString(), "timeout=5");
        }

        Calendar today = Calendar.getInstance();
        response.headers().set(HttpHeaderNames.DATE, today);
        response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        response.headers().set(HttpHeaderNames.VARY, "Origin");

        ctx.writeAndFlush(response);
    }

    private void handleWebSocketPath(ChannelHandlerContext ctx, String path, Object msg) {

        SocketAddress remoteAddress = ctx.pipeline().channel().remoteAddress();
        InetSocketAddress remote = (InetSocketAddress) remoteAddress;

        log.debug("WebSocketServerHandler(" + remote + ") handleService() path:" + path);


        sockJsTransport.doOpen(ctx, (HttpRequest)msg, getWebSocketLocation(ctx.pipeline(),(HttpRequest)msg), path);

    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, Object frame) {


        if(!(frame instanceof  WebSocketFrame)){
            log.info("Frame is not WebSocketFrame" + ctx.channel());
            return;
        }

        WebSocketFrame wFrame =  (WebSocketFrame) frame;

        if (wFrame instanceof TextWebSocketFrame) {
            sockJsTransport.onMessage(ctx, wFrame);
            return;
        }
        if (wFrame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(wFrame.content().retain()));
            return;
        }
        if (wFrame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(wFrame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (wFrame instanceof BinaryWebSocketFrame) {
            sockJsTransport.onBinary(ctx, wFrame);
            return;
        }
        if (wFrame instanceof PongWebSocketFrame) {
            return;
        }
    }

    private String getWebSocketLocation(ChannelPipeline pipeline, HttpRequest req) {
        boolean isSsl = pipeline.get(SslHandler.class) != null;

        String host = req.headers().get(HttpHeaderNames.HOST)==null?req.uri():req.headers().get(HttpHeaderNames.HOST);

        if (isSsl) {
            return "wss://" + host + wsPath;
        } else {
            return "ws://" + host + wsPath;
        }
    }


}