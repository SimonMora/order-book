package com.gaudiy.orderbook.config;

import com.gaudiy.orderbook.commons.utils.Constants;
import com.gaudiy.orderbook.commons.utils.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.java_websocket.client.WebSocketClient;
import org.springframework.context.annotation.Primary;

import java.net.URI;

@Configuration
public class WebSocketBtcClientConfig {
    @Value("${binance.ws.uri}")
    private String wsBinanceServerUri;

    @Value(Constants.CURRENCY_BTC)
    private String currency;

    @Bean
    @Primary
    @Qualifier("WebSocketClientBTC")
    public WebSocketClient webSocketClientBTC() {
        return new OrderWebSocketClient(
                URI.create(Utils.buildBinanceUri(currency, wsBinanceServerUri, true)),
                currency
        );
    }
}
