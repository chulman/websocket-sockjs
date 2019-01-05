package com.chulm.websocket.spring.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Websocket은 Spring 4 이상 부터 지원.
 * @link https://spring.io/guides/gs/messaging-stomp-websocket/
 * @ref https://supawer0728.github.io/2018/03/30/spring-websocket/
 * */
@SpringBootApplication
public class RunApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunApplication.class);
    }
}
