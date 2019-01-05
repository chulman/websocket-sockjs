package com.chulm.websocket.spring.server.configuration;

import com.chulm.websocket.spring.server.handler.WsTranportHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//spirng 의 장점은 high-level api를 통한 간단한 구성.
//아래와 같은 설정은 직접 소켓에 대한 처리를 다루는 설정.
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WsTranportHandler(), "/ws-handler").setAllowedOrigins("*").withSockJS();
    }
}

