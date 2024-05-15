package com.gaudiy.orderbook.config;

import com.gaudiy.orderbook.utils.Constants;
import com.gaudiy.orderbook.utils.Utils;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class WebSocketEthClientConfig {
    @Value("${binance.ws.uri}")
    private String wsBinanceServerUri;

    @Value(Constants.CURRENCY_ETH)
    private String currency;

    @Bean
    @Qualifier("WebSocketClientETH")
    public WebSocketClient webSocketClientETH() {
        return new OrderWebSocketClient(
                URI.create(Utils.buildBinanceUri(currency, wsBinanceServerUri, true)),
                currency
        );
    }
}
