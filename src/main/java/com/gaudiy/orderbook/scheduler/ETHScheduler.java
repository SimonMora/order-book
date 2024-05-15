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
public class ETHScheduler {

    private WebSocketClient webSocketClientETH;

    private OrderBookService orderBookService;

    @Scheduled(fixedDelay = 10000)
    public void scheduledETHOrderBook() {
        if (!webSocketClientETH.isOpen()) {
            webSocketClientETH.connect();
        }
        System.out.println("Start scheduler for ETH order book printing");
        orderBookService.orderBookUpdate(Constants.CURRENCY_ETH);
    }

    @Autowired
    public ETHScheduler(@Qualifier("WebSocketClientETH") WebSocketClient webSocketClientETH, OrderBookService orderBookService) {
        this.webSocketClientETH = webSocketClientETH;
        this.orderBookService = orderBookService;
    }
}
