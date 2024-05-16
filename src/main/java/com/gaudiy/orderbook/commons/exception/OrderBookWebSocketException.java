package com.gaudiy.orderbook.commons.exception;

public class OrderBookWebSocketException extends OrderApplicationException {

    public OrderBookWebSocketException(String currency) {
        super(currency);
    }

    public OrderBookWebSocketException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            String currency
    ) {
        super(message, cause, enableSuppression, writableStackTrace, currency);
    }

    public OrderBookWebSocketException(String message, Throwable cause, String currency) {
        super(message, cause, currency);
    }

    public OrderBookWebSocketException(String message, String currency) {
        super(message, currency);
    }

    public OrderBookWebSocketException(Throwable cause, String currency) {
        super(cause, currency);
    }


}
