package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.entity.OrderBook;
import com.gaudiy.orderbook.entity.Snapshot;
import com.gaudiy.orderbook.event.OrderBookOutOfSyncEvent;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.service.RecordService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class OrderBookServiceImpl implements OrderBookService, ApplicationEventPublisherAware {

    private final Logger LOG = Logger.getLogger("OrderBookServiceImpl.class");

    final BTCRecordsStorage storage = BTCRecordsStorage.getInstance();

    private ApplicationEventPublisher publisher;

    @Value("${binance.depth.snapshot.uri}")
    private String depthSnapshotUri;

    @Autowired
    private RecordService recordService;

    @Override
    public void printOrderBook(Long orderBookId) throws IllegalArgumentException {
        try {
            final var orderBooks = storage.getOrderBooks();
            var orderBooksFiltered = orderBooks
                    .stream()
                    .filter(ob -> ob.getLastUpdateId().equals( orderBookId))
                    .limit(2)
                    .collect(Collectors.toList());
            if (orderBooksFiltered.size() > 1) {
                throw new IllegalArgumentException("There are two orders with the same lastUpdateId..");
            }

            final OrderBook orderToProcess = orderBooksFiltered.get(0);
            final var asks = orderToProcess.getAsks();
            final var bids = orderToProcess.getBids();

            if (!asks.isEmpty() && !bids.isEmpty()) {
                orderBooksFiltered = orderBooks
                        .stream()
                        .filter(ob -> ob.getLastUpdateId().equals( orderToProcess.getInitialUpdateId() ))
                        .limit(2)
                        .collect(Collectors.toList());
                if (orderBooks.size() > 1 && orderBooksFiltered.size() > 1) {
                    throw new IllegalArgumentException("There are two orders with the same initialUpdateId..");
                }
                final OrderBook orderToCompare = orderBooksFiltered.get(0);

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

                var volumeDiffer = orderToProcess.getTotalVolume().subtract(orderToCompare.getTotalVolume());

                System.out.println("\u001B[33mTotal current volume variation: " + volumeDiffer);
                System.out.println("\u001B[0m");
            } else {
                System.out.println("System is retrieving orders, please wait..");
            }
        } catch (IllegalArgumentException e) {
            LOG.severe(e.getMessage());
            publisher.publishEvent(new OrderBookOutOfSyncEvent());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void orderBookUpdate() {
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(depthSnapshotUri))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            Snapshot latestSnapshot = gson.fromJson(response.body(), Snapshot.class);

            recordService.orderUpdate(latestSnapshot.getLastUpdateId());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
