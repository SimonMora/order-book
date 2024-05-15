package com.gaudiy.orderbook.config;

import com.gaudiy.orderbook.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;

@Configuration
public class WebSocketClientConfig {

    @Value("${binance.ws.uri}")
    private String wsBinanceServerUri;

    @Value("${currency.process:BTC}")
    private String currency;

    @Bean("WebSocketClientBTC")
    public WebSocketClient webSocketClient() {
        return new OrderWebSocketClient(URI.create(Utils.buildBinanceUri(currency, wsBinanceServerUri, true)));
    }
}
