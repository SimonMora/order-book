package com.gaudiy.orderbook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.java_websocket.client.WebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.net.URI;

@Configuration
public class WebSocketClientConfig {

    @Value("${binance.ws.uri}")
    private String binanceServerUri;

    @Bean
    public WebSocketClient webSocketClient() {
        System.out.println(binanceServerUri);
        return new OrderWebSocketClient(URI.create(binanceServerUri));
    }
}
