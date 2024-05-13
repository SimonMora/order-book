package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.entity.OrderBook;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.OrderBookService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderBookServiceImpl implements OrderBookService {

    final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();

    @Override
    public void printOrderBook() {
        final var bids = storage.getBidsMap();
        final var asks = storage.getAsksMap();

        OrderBook orderBook = new OrderBook(bids, asks);

        if (!asks.isEmpty() && !bids.isEmpty()) {
            List<String> bidsList = new ArrayList<>();
            List<String> asksList = new ArrayList<>();

            bidsList.add("\u001B[32mBuy");
            asksList.add("\u001B[31mSell");

            bidsList.addAll( bids
                    .keySet()
                    .stream()
                    .sorted()
                    .map(key -> {
                        final StringBuilder sb = new StringBuilder("\u001B[32m");
                        sb.append(String.format("%-10.10s  %-10.10s", key, bids.get(key)));
                        sb.append("\u001B[0m");
                        return sb.toString();
                    })
                    .collect(Collectors.toList())
            );
            asksList.addAll( asks
                    .keySet()
                    .stream()
                    .sorted()
                    .map(key -> {
                        final StringBuilder sb = new StringBuilder("\u001B[31m");
                        sb.append(String.format("%-10.10s  %-10.10s", key, asks.get(key)));
                        sb.append("\u001B[0m");
                        return sb.toString();
                    })
                    .collect(Collectors.toList())
            );

            if (bidsList.size() > asksList.size()) {
                while (bidsList.size() > asksList.size())
                    asksList.add("");
            } else {
                while (bidsList.size() < asksList.size())
                    bidsList.add("");
            }

            for (int i = 0; i <= bids.size(); i++) {
                System.out.printf("%-30.32s    %-30.32s%n", bidsList.get(i), asksList.get(i));
            }

            var oldOrder = storage.getOrderBooks().poll();
            var newOrder = storage.getOrderBooks().peek();
            var volumeDiffer = newOrder != null ?
                    newOrder.getTotalVolume().subtract(oldOrder.getTotalVolume()) : oldOrder.getTotalVolume();

            System.out.println("\u001B[33mTotal volume in the current order book: " + volumeDiffer);
            System.out.println("\u001B[0m");
        } else {
            System.out.println("System is retrieving orders, please wait..");
        }

    }
}
