package com.gaudiy.orderbook.event;

public class OrderBookUpdatedEvent {

    private Long lastOrderBookId;

    public OrderBookUpdatedEvent(Long lastOrderBookId) {
        this.lastOrderBookId = lastOrderBookId;
    }

    public Long getLastOrderBookId() {
        return lastOrderBookId;
    }

    @Override
    public String toString() {
        return "Application event: Order Book updated :: " + this.lastOrderBookId ;
    }

}
