package com.gaudiy.orderbook.event;

public record OrderBookUpdatedEvent(Long lastOrderBookId, String currency) {

    @Override
    public String toString() {
        return "Application event: Order Book updated :: " + this.lastOrderBookId;
    }

}
