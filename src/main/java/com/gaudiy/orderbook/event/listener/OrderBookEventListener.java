package com.gaudiy.orderbook.event.listener;

import com.gaudiy.orderbook.commons.utils.Constants;
import com.gaudiy.orderbook.event.OrderBookOutOfSyncEvent;
import com.gaudiy.orderbook.event.OrderBookUpdatedEvent;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.commons.utils.Utils;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class OrderBookEventListener {

    private static final Logger LOG = Logger.getLogger("OrderBookEventListener.class");

    private OrderBookService orderBookService;

    private WebSocketClient webSocketClientBTC;

    private WebSocketClient webSocketClientETH;

    @EventListener
    void handleOrderBookUpdate(OrderBookUpdatedEvent event) {
        try {
            final Long orderBookToProcess = event.lastOrderBookId();
            final String currency = event.currency();
            var storage = Utils.retrieveStorage(currency);

            storage.saveOrderBookAndResetPriceLevels(orderBookToProcess);
            orderBookService.printOrderBook(orderBookToProcess, currency);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    void handleOrderBookOutOfSync(OrderBookOutOfSyncEvent event) {
        LOG.severe(event.errorCause());
        LOG.severe(event.currency());

        try {
            var storage = Utils.retrieveStorage(event.currency());
            storage.restoreStorage();
            if (event.currency().equals(Constants.CURRENCY_BTC)) {
                webSocketClientBTC.reconnect();
            } else {
                webSocketClientETH.reconnect();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public OrderBookEventListener(
            OrderBookService orderBookService,
            @Qualifier("WebSocketClientBTC") WebSocketClient webSocketClientBTC,
            @Qualifier("WebSocketClientETH") WebSocketClient webSocketClientETH
    ) {
        this.orderBookService = orderBookService;
        this.webSocketClientBTC = webSocketClientBTC;
        this.webSocketClientETH = webSocketClientETH;
    }
}
