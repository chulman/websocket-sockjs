package com.chulm.websocket.netty.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@PropertySource(value = "classpath:/application.properties")
public class WebSocketServer {

    private long timeout = 3000;

    @Value("${ws.host}")
    private String host = "0.0.0.0";

    @Value("${ws.port}")
    private int port = 8080;

    @Value("${ws.path}")
    private String wsPath;

    @Value("${ws.boss.thread.count}")
    private int bossThreadCount;

    @Value("${ws.https}")
    private boolean isHttps = false;

//    @Value("${websocket.certpath}")
    private String certPath = "";

//    @Value("${websocket.certpassword}")
    private String certPassword = "";

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    private ChannelFuture future;

    public void bind(){
        bossGroup = new NioEventLoopGroup(bossThreadCount);
        workerGroup = new NioEventLoopGroup();

        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        WebsocketChannelInitializer initializer = new WebsocketChannelInitializer();

        initializer.setWsPath(wsPath);
        initializer.setTimeout(timeout);
        initializer.setPort(port);

        initializer.setHttps(isHttps());
        initializer.setCertPath(certPath);
        initializer.setCertPassword(certPassword);

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_RCVBUF, 43800)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childHandler(initializer);


        try {
            future = bootstrap.bind(host,port).sync();
            log.info("Websocket Netty Server Started : {}" , future.channel().localAddress() + wsPath);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void shutdown() throws InterruptedException {
        future.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
