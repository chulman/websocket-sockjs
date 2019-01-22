package com.chulm.websocket.netty.server.handler;

import com.chulm.websocket.netty.server.handler.transports.SockJsTransport;
import com.chulm.websocket.netty.server.utils.SockJsResponse;
import com.chulm.websocket.netty.server.utils.UrlUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Calendar;

@Slf4j
public class RouterHandler extends ChannelInboundHandlerAdapter {


    private String wsPath;
    private boolean isHttps;
    private final SockJsTransport sockJsTransport;

    public RouterHandler(String wsPath, boolean isHttps, SockJsTransport sockJsTransport) {

        this.wsPath = wsPath;
        this.isHttps = isHttps;
        this.sockJsTransport = sockJsTransport;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("{} read message=<{}>", getClass().getName(), msg);
        if (msg instanceof HttpRequest) {
            handleHttpPath(ctx, msg);
        }
    }

    private void handleHttpPath(ChannelHandlerContext ctx, Object msg) {
        SocketAddress remoteAddress = ctx.pipeline().channel().remoteAddress();
        InetSocketAddress remote = (InetSocketAddress) remoteAddress;
        HttpRequest request = (HttpRequest) msg;
        String uri = request.uri();

        request.setUri(uri.replaceFirst(wsPath, ""));
        QueryStringDecoder qsd = new QueryStringDecoder(request.uri());
        String path = qsd.path();

        log.info("RouterHandler(" + remote + ") handleService() path:" + path);

        if (path.startsWith(UrlUtils.START_WITH_INFO_PATH)) {
            sendInfoResponse(ctx, request);
        } else {
            handleWebSocketPath(ctx, path, msg);
        }
    }

    private void sendInfoResponse(ChannelHandlerContext ctx, HttpRequest request) {
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

        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().close();
            }
        });
    }

    private void handleWebSocketPath(ChannelHandlerContext ctx, String path, Object msg) {

        SocketAddress remoteAddress = ctx.pipeline().channel().remoteAddress();
        InetSocketAddress remote = (InetSocketAddress) remoteAddress;

        log.info("RoutHandler({}) handleService() path:{}",remote,path);

        sockJsTransport.doOpen(ctx, (HttpRequest) msg, getWebSocketLocation(ctx.pipeline(), (HttpRequest) msg), path);

    }

    private String getWebSocketLocation(ChannelPipeline pipeline, HttpRequest req) {
        boolean isSsl = pipeline.get(SslHandler.class) != null;

        String host = req.headers().get(HttpHeaderNames.HOST) == null ? req.uri() : req.headers().get(HttpHeaderNames.HOST);

        if (isSsl) {
            return "wss://" + host + wsPath;
        } else {
            return "ws://" + host + wsPath;
        }
    }
}
