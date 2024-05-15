package com.gaudiy.orderbook.scheduler;

import com.gaudiy.orderbook.service.OrderBookService;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
public class Schedulers {

    @Autowired
    private WebSocketClient webSocketClient;

    @Autowired
    private OrderBookService orderBookService;

    @Scheduled(fixedDelay = 10000)
    public void scheduledBTCOrderBook() {
        if (!webSocketClient.isOpen()) {
            webSocketClient.connect();
        }
        System.out.println("Start scheduler for BTC order book printing");
        orderBookService.orderBookUpdate();
    }

}
