package com.chulm.websocket.sockjs.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class RunSockJsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunSockJsWebApplication.class, args);
    }
}