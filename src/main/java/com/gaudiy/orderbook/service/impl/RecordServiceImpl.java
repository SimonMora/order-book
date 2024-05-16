package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.commons.exception.OrderApplicationException;
import com.gaudiy.orderbook.entity.Record;
import com.gaudiy.orderbook.event.OrderBookUpdatedEvent;
import com.gaudiy.orderbook.service.RecordService;
import com.gaudiy.orderbook.commons.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Service
@EnableAsync
public class RecordServiceImpl implements RecordService, ApplicationEventPublisherAware {

    private static final Logger LOG = Logger.getLogger("RecordServiceImpl.class");

    private ApplicationEventPublisher publisher;

    @Override
    public void manageNewRecordReceived(String object, String currency) {
        final Gson parser = new Gson();
        var storage = Utils.retrieveStorage(currency);

        try {
            final Record newRecord = parser.fromJson(object, Record.class);
            storage.storeReceivedRecord(newRecord);
        } catch (JsonSyntaxException e) {
            LOG.severe("Object received: " + object);
            throw new OrderApplicationException(e, currency);
        }
    }

    @Override
    @Async
    public void parseRecordPrices(Record record, String currency) {
        try {
            record
                    .getBids()
                    .stream()
                    .parallel()
                    .forEach(bid -> { this.evaluateBidProcessing(bid, currency); });
            record
                    .getAsks()
                    .stream()
                    .parallel()
                    .forEach(ask -> { this.evaluateAskProcessing(ask, currency); });
        } catch (ConcurrentModificationException e) {
            throw new OrderApplicationException(e, currency);
        }
    }

    @Override
    public void orderUpdate(Long lastUpdateId, String currency) {
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            var records = Utils.retrieveStorage(currency).getRecordList();

            if (!records.isEmpty()) {
                records.stream()
                        .parallel()
                        .forEach(record -> {
                            parseRecordPrices(record, currency);
                            counter.incrementAndGet();
                            if(records.size() == counter.get()) {
                                publisher.publishEvent(new OrderBookUpdatedEvent(lastUpdateId, currency));
                            }
                        });
            } else {
                System.out.println("No records to process..");
            }
        } catch (ConcurrentModificationException e) {
            throw new OrderApplicationException(e, currency);
        }
    }

    @Async
    private void evaluateBidProcessing(List<String> bid, String currency) {
        var storage = Utils.retrieveStorage(currency);
        try {
            if(bid != null && !storage.isBidStored(bid.get(0))) {
                storage.addNewBids(bid);
            } else {
                storage.updateBid(bid.get(0), bid.get(1));
            }
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("Received unparseable price levels in the Record from binance ws");
            throw new OrderApplicationException(e, currency);
        }
    }

    @Async
    private void evaluateAskProcessing(List<String> ask, String currency) {
        var storage = Utils.retrieveStorage(currency);
        try {
            if(ask != null && !storage.isAskStored(ask.get(0))) {
                storage.addNewAsks(ask);
            } else {
                storage.updateAsk(ask.get(0), ask.get(1));
            }
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("Received unparseable price levels in the Record from binance ws");
            throw new OrderApplicationException(e, currency);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
