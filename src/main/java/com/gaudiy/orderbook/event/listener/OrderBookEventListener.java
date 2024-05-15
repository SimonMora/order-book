package com.gaudiy.orderbook.event.listener;

import com.gaudiy.orderbook.event.OrderBookOutOfSyncEvent;
import com.gaudiy.orderbook.event.OrderBookUpdatedEvent;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.OrderBookService;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderBookEventListener {

    @Autowired
    private OrderBookService orderBookService;

    final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();

    @Autowired
    private WebSocketClient webSocketClient;

    @EventListener
    void handleOrderBookUpdate(OrderBookUpdatedEvent event) {
        try {
            final Long orderBookToProcess = event.lastOrderBookId();
            storage.saveOrderBookAndResetPriceLevels(orderBookToProcess);
            orderBookService.printOrderBook(orderBookToProcess);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    void handleOrderBookOutOfSync(OrderBookOutOfSyncEvent event) {
        try {
            storage.restoreStorage();
            webSocketClient.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
