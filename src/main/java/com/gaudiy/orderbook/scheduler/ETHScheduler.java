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
public class ETHScheduler {

    private static final Logger LOG = Logger.getLogger("ETHScheduler.class");

    private WebSocketClient webSocketClientETH;

    private OrderBookService orderBookService;

    @Scheduled(fixedDelay = 10000)
    public void scheduledETHOrderBook() {
        try {
            if (!webSocketClientETH.isOpen()) {
                webSocketClientETH.connect();
            }
            orderBookService.orderBookUpdate(Constants.CURRENCY_ETH);
        } catch (IllegalStateException e) {
            LOG.severe("ETH WebSocket Disconnected from source.");
            throw new OrderBookWebSocketException(e.getMessage(), Constants.CURRENCY_ETH);
        }
    }

    @Autowired
    public ETHScheduler(@Qualifier("WebSocketClientETH") WebSocketClient webSocketClientETH, OrderBookService orderBookService) {
        this.webSocketClientETH = webSocketClientETH;
        this.orderBookService = orderBookService;
    }
}
