package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.entity.Record;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.RecordService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableAsync
public class RecordServiceImpl implements RecordService {

    @Override
    public void manageNewRecordReceived(String object) {
        final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();
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
    public void parseRecordPrices(Record record) {
        //System.out.println("number of bids: " + record.getBids().size());
        //System.out.println("number of asks: " + record.getAsks().size());

        record.getBids().forEach(this::evaluateBidProcessing);
        record.getAsks().forEach(this::evaluateAskProcessing);
    }

    private void evaluateBidProcessing(List<String> bid) {
        final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();

        if(bid != null && !storage.isBidStored(bid.get(0))) {
            storage.addNewBids(bid);
        } else {
            storage.updateBid(bid.get(0), bid.get(1));
        }
    }

    private void evaluateAskProcessing(List<String> ask) {
        final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();
        //System.out.println(ask);
        if(ask != null && !storage.isAskStored(ask.get(0))) {
            storage.addNewAsks(ask);
        } else {
            storage.updateAsk(ask.get(0), ask.get(1));
        }
    }
}
