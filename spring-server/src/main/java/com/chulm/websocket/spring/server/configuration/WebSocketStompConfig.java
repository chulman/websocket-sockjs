package com.chulm.websocket.spring.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
//메세지 브로커에 의해 websocket message를 다루게한다.
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    //messageBroker 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //메모리 기반 messageBroker가 해당 목적지를 구독하고있는 클라이언트에게 응답. pub/sub
        config.enableSimpleBroker("/topic");
        // api destination의 접두사 설정.  ex) /app/hello
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        WebSocket을 사용할 수없는 경우 대체 전송을 사용할 수 있도록 SockJS 폴백 옵션을 활성화합니다.
//        SockJS 클라이언트는 "/wsapi"에 연결하여 사용 가능한 최상의 전송 (websocket, xhr-streaming, xhr-polling 등)을 시도.
        registry.addEndpoint("/ws/api").setAllowedOrigins("*").withSockJS();
    }
}
