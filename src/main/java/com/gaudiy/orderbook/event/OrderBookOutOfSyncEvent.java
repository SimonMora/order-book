package com.gaudiy.orderbook.event;

public record OrderBookOutOfSyncEvent(String errorCause, String currency) {

    @Override
    public String toString() {
        return "OrderBookOutOfSyncEvent application error detected :: cause: " + this.errorCause;
    }
}
