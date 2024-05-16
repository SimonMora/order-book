package com.gaudiy.orderbook.handlers;

import com.gaudiy.orderbook.commons.exception.OrderApplicationException;
import com.gaudiy.orderbook.commons.exception.OrderBookWebSocketException;
import com.gaudiy.orderbook.commons.exception.OrderPricesWrongFormat;
import com.gaudiy.orderbook.event.OrderBookOutOfSyncEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ConcurrentModificationException;
import java.util.logging.Logger;

@Component
public class ApplicationExceptionHandler implements ApplicationEventPublisherAware {

    private final static Logger LOG = Logger.getLogger("ApplicationExceptionHandler.class");

    private ApplicationEventPublisher applicationEventPublisher;

    @ExceptionHandler(OrderBookWebSocketException.class)
    public void handleDisconnectedException(OrderBookWebSocketException exception) {
        LOG.severe(exception.getCurrency() + "WebSockets, is required to clean up the storage and re-start the process.");
        applicationEventPublisher.publishEvent(new OrderBookOutOfSyncEvent(exception.getMessage(), exception.getCurrency()));
    }

    @ExceptionHandler(OrderPricesWrongFormat.class)
    public void handleWrongPricesException(OrderPricesWrongFormat exception) {
        LOG.severe("Possible de-synchronization incoming, disconnection from WebSockets is expected.");
    }

    @ExceptionHandler(OrderApplicationException.class)
    public void handleOrderApplicationException(OrderApplicationException exception) {
        Throwable ex = exception.getCause();

        if (ex instanceof ConcurrentModificationException){
            LOG.severe("Errors in synchronization might have happened, is recommended to reject the following Order Book");
        }

        if (ex instanceof IndexOutOfBoundsException) {
            LOG.severe("Possible de-synchronization encountered, is required to clean up the storage and re-start the process.");
            applicationEventPublisher.publishEvent(new OrderBookOutOfSyncEvent(exception.getMessage(), exception.getCurrency()));
        }

        if(ex instanceof IllegalArgumentException) {
            LOG.severe("Possible de-synchronization encountered, is required to clean up the storage and re-start the process.");
            applicationEventPublisher.publishEvent(new OrderBookOutOfSyncEvent(exception.getMessage(), exception.getCurrency()));
        }

        if (ex instanceof Exception) {
            final String msg = String.format("Unexpected behavior encountered, please get in touch with an administrator." +
                    " Probide the following message: %n%s", ex.getMessage());
            LOG.severe(msg);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
