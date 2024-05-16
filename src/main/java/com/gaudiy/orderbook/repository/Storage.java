package com.gaudiy.orderbook.repository;

import com.gaudiy.orderbook.commons.exception.OrderApplicationException;
import com.gaudiy.orderbook.commons.exception.OrderPricesWrongFormat;
import com.gaudiy.orderbook.entity.OrderBook;
import com.gaudiy.orderbook.entity.Record;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Storage {

    private static final Logger LOG = Logger.getLogger("Storage.class");

    private String currency;
    private ConcurrentSkipListSet<Record> recordList;
    private Queue<OrderBook> orderBooks;
    private Map<String, String> bidsMap;
    private Map<String, String> asksMap;


    protected Storage(String currency) {
        this.currency = currency;
        bidsMap = new ConcurrentHashMap<>();
        asksMap = new ConcurrentHashMap<>();
        recordList = new ConcurrentSkipListSet<>();
        orderBooks = new ConcurrentLinkedQueue<>();
    }

    public void addNewBids(List<String> bid) {
        try {
            final double price = Double.parseDouble(bid.get(0));
            final double level = Double.parseDouble(bid.get(1));

            if (level > 0 && bidsMap.size() < 50) {
                bidsMap.put(bid.get(0), bid.get(1));
            }
        } catch (NumberFormatException e) {
            LOG.severe(e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("List prices with wrong format causes: " + e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        }
    }

    public void updateBid(String bidPrice, String bid) {
        try {
            final double price = Double.parseDouble(bidPrice);
            final double level = Double.parseDouble(bid);

            if (level == 0) {
                bidsMap.remove(bidPrice);
            } else {
                bidsMap.replace(bidPrice, bid);
            }
        } catch (NumberFormatException e) {
            LOG.severe(e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("List prices with wrong format causes: " + e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        }
    }

    public boolean isBidStored(String bidPrice){
        return bidsMap.get(bidPrice) != null;
    }

    public void addNewAsks(List<String> ask) {
        try {
           final double price = Double.parseDouble(ask.get(0));
           final double level = Double.parseDouble(ask.get(1));

            if (level > 0 && asksMap.size() < 50) {
                asksMap.put(ask.get(0), ask.get(1));
            }
        } catch (NumberFormatException e) {
            LOG.severe(e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("List prices with wrong format causes: " + e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        }
    }

    public void updateAsk(String askPrice, String ask) {
        try {
            final double price = Double.valueOf(askPrice);
            final double level = Double.valueOf(ask);

            if (level == 0) {
                asksMap.remove(askPrice);
            } else {
                asksMap.replace(askPrice, ask);
            }
        } catch (NumberFormatException e) {
            LOG.severe(e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            LOG.severe("List prices with wrong format causes: " + e.getMessage());
            throw new OrderPricesWrongFormat(e.getMessage());
        }
    }

    public boolean isAskStored(String askPrice){
        return asksMap.get(askPrice) != null;
    }


    public void storeReceivedRecord(Record newRecord) {
        if (newRecord != null) {
            if (!recordList.isEmpty()) {
                var pastRecord = recordList.last();
                if (pastRecord != null && pastRecord.getFinalUpdateId() + 1 == newRecord.getInitialUpdateId()) {
                    recordList.add(newRecord);
                } else {
                    throw new OrderApplicationException(new IllegalArgumentException("De-synchronized exchanged and local order book"), currency);
                }
            } else {
                recordList.add(newRecord);
            }
        }
    }

    public void cleanRecordList(Long lastUpdateId) {
        this.recordList = recordList
                .stream()
                .filter(record -> record.getFinalUpdateId() > lastUpdateId)
                .collect(Collectors.toCollection(() -> new ConcurrentSkipListSet<>()));
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
        recordList = new ConcurrentSkipListSet<>();
        orderBooks = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentSkipListSet<Record> getRecordList() {
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
