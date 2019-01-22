package com.chulm.websocket.netty.server;

import com.chulm.websocket.netty.server.server.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *  JVM_Option
 * -Dio.netty.leakDetection.level=advanced
 */
@SpringBootApplication
public class RunNettyApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RunNettyApplication.class, args);
        WebSocketServer webSocketServer = context.getBean(WebSocketServer.class);
        webSocketServer.bind();

    }
}
