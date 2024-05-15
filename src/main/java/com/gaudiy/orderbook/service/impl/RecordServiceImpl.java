package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.entity.Record;
import com.gaudiy.orderbook.event.OrderBookUpdatedEvent;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.RecordService;
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

    private final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();
    private ApplicationEventPublisher publisher;

    @Override
    public void manageNewRecordReceived(String object) {
        final Gson parser = new Gson();
        
        try {
            final Record newRecord = parser.fromJson(object, Record.class);
            storage.storeReceivedRecord(newRecord);
        } catch (JsonSyntaxException e) {
            System.out.println("Object received: " + object);
            System.out.println(e.getMessage());
        }
    }

    @Override
    @Async
    public void parseRecordPrices(Record record) {
        try {
            record
                    .getBids()
                    .stream()
                    .parallel()
                    .forEach(this::evaluateBidProcessing);
            record
                    .getAsks()
                    .stream()
                    .parallel()
                    .forEach(this::evaluateAskProcessing);
        } catch (ConcurrentModificationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void orderUpdate(Long lastUpdateId) {
        final AtomicInteger counter = new AtomicInteger(0);
        final var records = storage.getRecordList();
        if (records.size() > 0) {
            records.stream()
                    .parallel()
                    .forEach(record -> {
                        parseRecordPrices(record);
                        counter.incrementAndGet();
                        if(records.size() == counter.get()) {
                            publisher.publishEvent(new OrderBookUpdatedEvent(lastUpdateId));
                        }
                    });
        } else {
            System.out.println("No records to process..");
        }
    }

    @Async
    private void evaluateBidProcessing(List<String> bid) {
        try {
            if(bid != null && !storage.isBidStored(bid.get(0))) {
                storage.addNewBids(bid);
            } else {
                storage.updateBid(bid.get(0), bid.get(1));
            }
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("Received unparseable price levels in the Record from binance ws");
            throw e;
        }
    }

    @Async
    private void evaluateAskProcessing(List<String> ask) {
        try {
            if(ask != null && !storage.isAskStored(ask.get(0))) {
                storage.addNewAsks(ask);
            } else {
                storage.updateAsk(ask.get(0), ask.get(1));
            }
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("Received unparseable price levels in the Record from binance ws");
            throw e;
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
