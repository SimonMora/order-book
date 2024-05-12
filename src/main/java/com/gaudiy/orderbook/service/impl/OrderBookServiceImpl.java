package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.OrderBookService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderBookServiceImpl implements OrderBookService {
    @Override
    public void printOrderBook() {
        final var bids = BTCRecordsStorage.getInstance().getBidsMap();
        final var asks = BTCRecordsStorage.getInstance().getAsksMap();

        if (!asks.isEmpty() && !bids.isEmpty()) {
            final List<String> bidsList = new ArrayList<>();
            final List<String> asksList = new ArrayList<>();

            bidsList.add("\u001B[32mBuy");
            asksList.add("\u001B[31mSell");

            bids.keySet().forEach(key -> {
                final StringBuilder sb = new StringBuilder("\u001B[32m");
                sb.append(String.format("%-10.16s  %-10.16s", key, bids.get(key)));
                sb.append("\u001B[0m");
                bidsList.add(sb.toString());
            });
            asks.keySet().forEach(key -> {
                final StringBuilder sb = new StringBuilder("\u001B[31m");
                sb.append(String.format("%-10.16s  %-10.16s", key, asks.get(key)));
                sb.append("\u001B[0m");
                asksList.add(sb.toString());
            });

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
            System.out.println("\u001B[0m");
        } else {
            System.out.println("System is retrieving orders, please wait..");
        }

    }
}
