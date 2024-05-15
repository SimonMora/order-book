package com.gaudiy.orderbook.event;

public class OrderBookOutOfSyncEvent {
    public OrderBookOutOfSyncEvent() {
    }

    @Override
    public String toString() {
        return "OrderBookOutOfSyncEvent application error detected.";
    }
}
