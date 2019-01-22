package com.chulm.websocket.spring.server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    //messageBroker config
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
        scheduler.initialize();

        //in-memory message-broker
         config.enableSimpleBroker("/topic")
               .setHeartbeatValue(new long[]{5000, 5000})       //default 10000, 10000
               .setTaskScheduler(scheduler);


        // prefix mapping-message uri
        config.setApplicationDestinationPrefixes("/api");

    };



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        WebSocket을 사용할 수없는 경우 대체 전송을 사용할 수 있도록 SockJS 폴백 옵션을 활성화합니다.
//        SockJS 클라이언트는 "/ws/api"에 연결하여 사용 가능한 최상의 전송 (websocket, xhr-streaming, xhr-polling 등)을 시도.
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
