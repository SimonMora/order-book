package com.gaudiy.orderbook.commons.exception;

public class OrderPricesWrongFormat extends RuntimeException {

    public OrderPricesWrongFormat() {
        super();
    }

    public OrderPricesWrongFormat(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public OrderPricesWrongFormat(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderPricesWrongFormat(String message) {
        super(message);
    }

    public OrderPricesWrongFormat(Throwable cause) {
        super(cause);
    }
}
