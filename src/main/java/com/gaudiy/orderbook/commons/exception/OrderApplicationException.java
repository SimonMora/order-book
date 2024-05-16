package com.gaudiy.orderbook.commons.exception;

public class OrderApplicationException extends RuntimeException {

    private final String currency;

    public OrderApplicationException(String currency) {
        super();
        this.currency = currency;
    }

    public OrderApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String currency) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.currency = currency;
    }

    public OrderApplicationException(String message, Throwable cause, String currency) {
        super(message, cause);
        this.currency = currency;
    }

    public OrderApplicationException(String message, String currency) {
        super(message);
        this.currency = currency;
    }

    public OrderApplicationException(Throwable cause, String currency) {
        super(cause);
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
