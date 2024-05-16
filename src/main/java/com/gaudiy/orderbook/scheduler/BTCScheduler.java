package com.gaudiy.orderbook.scheduler;

import com.gaudiy.orderbook.event.OrderBookOutOfSyncEvent;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.commons.utils.Constants;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.logging.Logger;


@Configuration
@EnableScheduling
public class BTCScheduler implements ApplicationEventPublisherAware {

    private static final Logger LOG = Logger.getLogger("BTCScheduler.class");

    private ApplicationEventPublisher applicationEventPublisher;

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
            LOG.severe(Constants.CURRENCY_BTC + "WebSockets, is required to clean up the storage and re-start the process.");
            applicationEventPublisher.publishEvent(new OrderBookOutOfSyncEvent(e.getMessage(), Constants.CURRENCY_BTC));
        }
    }

    @Autowired
    public BTCScheduler(@Qualifier("WebSocketClientBTC") WebSocketClient webSocketClient, OrderBookService orderBookService) {
        this.webSocketClient = webSocketClient;
        this.orderBookService = orderBookService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
