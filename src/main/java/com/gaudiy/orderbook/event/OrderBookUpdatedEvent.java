package com.gaudiy.orderbook.event;

public record OrderBookUpdatedEvent(Long lastOrderBookId) {

    @Override
    public String toString() {
        return "Application event: Order Book updated :: " + this.lastOrderBookId;
    }

}
