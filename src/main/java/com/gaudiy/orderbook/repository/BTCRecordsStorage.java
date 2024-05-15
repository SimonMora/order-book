package com.gaudiy.orderbook.repository;

import com.gaudiy.orderbook.entity.OrderBook;
import com.gaudiy.orderbook.entity.Record;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public final class BTCRecordsStorage {

    private static BTCRecordsStorage INSTANCE;
    private Set<Record> recordList;
    private Queue<OrderBook> orderBooks;
    private Map<String, String> bidsMap;
    private Map<String, String> asksMap;


    private BTCRecordsStorage() {
        bidsMap = new ConcurrentHashMap<>();
        asksMap = new ConcurrentHashMap<>();
        recordList = new HashSet<>();
        orderBooks = new ConcurrentLinkedQueue<>();
    }

    public static BTCRecordsStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BTCRecordsStorage();
        }

        return INSTANCE;
    }

    public void addNewBids(List<String> bid) {
        try {
            final double price = Double.parseDouble(bid.get(0));
            final double level = Double.parseDouble(bid.get(1));

            if (level > 0 && bidsMap.size() < 50) {
                bidsMap.put(bid.get(0), bid.get(1));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String updateBid(String bidPrice, String bid) {
        final BigDecimal price = BigDecimal.valueOf(Double.parseDouble(bidPrice));
        final Double level = Double.parseDouble(bid);

        if (level == 0) {
            bidsMap.remove(bidPrice);
        } else {
            bidsMap.replace(bidPrice, bid);
        }

        return bidPrice;
    }

    public boolean isBidStored(String bidPrice){
        return bidsMap.get(bidPrice) != null;
    }

    public void addNewAsks(List<String> ask) {
        try {
           final BigDecimal price = BigDecimal.valueOf(Double.parseDouble(ask.get(0)));
           final Double level = Double.parseDouble(ask.get(1));

            if (level > 0 && asksMap.size() < 50) {
                asksMap.put(ask.get(0), ask.get(1));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public String updateAsk(String askPrice, String ask) {
        final BigDecimal price = BigDecimal.valueOf(Double.valueOf(askPrice));
        final Double level = Double.valueOf(ask);

        if (level == 0) {
            asksMap.remove(askPrice);
        } else {
            asksMap.replace(askPrice, ask);
        }

        return askPrice;
    }

    public boolean isAskStored(String askPrice){
        return asksMap.get(askPrice) != null;
    }


    public void storeReceivedRecord(Record newRecord) {
        if (newRecord != null) {
            recordList.add(newRecord);
        }
    }

    public void cleanRecordList(Long lastUpdateId) {
        this.recordList = recordList
                .stream()
                .filter(record -> record.getFinalUpdateId() > lastUpdateId)
                .collect(Collectors.toSet());
    }

    public String saveOrderBookAndResetPriceLevels(Long lastUpdateId) {
        orderBooks.add(
                new OrderBook(
                        this.bidsMap,
                        this.asksMap,
                        lastUpdateId,
                        orderBooks.size() > 0 ? orderBooks.peek().getLastUpdateId() : 0
                )
        );

        bidsMap = new ConcurrentHashMap<>();
        asksMap = new ConcurrentHashMap<>();

        cleanRecordList(lastUpdateId);
        return lastUpdateId.toString();
    }

    public void restoreStorage() {
        bidsMap = new ConcurrentHashMap<>();
        asksMap = new ConcurrentHashMap<>();
        recordList = new HashSet<>();
        orderBooks = new ConcurrentLinkedQueue<>();
    }

    public Set<Record> getRecordList() {
        return recordList;
    }

    public Map<String, String> getAsksMap() {
        return asksMap;
    }

    public Map<String, String> getBidsMap() {
        return bidsMap;
    }

    public Queue<OrderBook> getOrderBooks() {
        return orderBooks;
    }

}
