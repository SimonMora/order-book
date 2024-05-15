package com.gaudiy.orderbook.scheduler;

import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.utils.Constants;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
public class BTCScheduler {

    private WebSocketClient webSocketClient;

    private OrderBookService orderBookService;

    @Scheduled(fixedDelay = 10000)
    public void scheduledBTCOrderBook() {
        if (!webSocketClient.isOpen()) {
            webSocketClient.connect();
        }
        System.out.println("Start scheduler for BTC order book printing");
        orderBookService.orderBookUpdate(Constants.CURRENCY_BTC);
    }

    @Autowired
    public BTCScheduler(@Qualifier("WebSocketClientBTC") WebSocketClient webSocketClient, OrderBookService orderBookService) {
        this.webSocketClient = webSocketClient;
        this.orderBookService = orderBookService;
    }
}
