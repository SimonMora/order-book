package com.gaudiy.orderbook.scheduler;

import com.gaudiy.orderbook.commons.exception.OrderBookWebSocketException;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.commons.utils.Constants;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.logging.Logger;


@Configuration
@EnableScheduling
public class BTCScheduler {

    private static final Logger LOG = Logger.getLogger("BTCScheduler.class");

    private WebSocketClient webSocketClient;

    private OrderBookService orderBookService;

    @Scheduled(fixedDelay = 10000)
    public void scheduledBTCOrderBook() {
        try {
            if (!webSocketClient.isOpen()) {
                webSocketClient.connect();
            }
            orderBookService.orderBookUpdate(Constants.CURRENCY_BTC);
        } catch (IllegalStateException e) {
            LOG.severe("BTC WebSocket Disconnected from source.");
            throw new OrderBookWebSocketException(e.getMessage(), Constants.CURRENCY_BTC);
        }
    }

    @Autowired
    public BTCScheduler(@Qualifier("WebSocketClientBTC") WebSocketClient webSocketClient, OrderBookService orderBookService) {
        this.webSocketClient = webSocketClient;
        this.orderBookService = orderBookService;
    }
}
