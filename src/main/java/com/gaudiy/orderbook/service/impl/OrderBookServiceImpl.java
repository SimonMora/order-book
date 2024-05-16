package com.gaudiy.orderbook.service.impl;

import com.gaudiy.orderbook.commons.exception.OrderApplicationException;
import com.gaudiy.orderbook.commons.utils.Constants;
import com.gaudiy.orderbook.entity.OrderBook;
import com.gaudiy.orderbook.entity.Snapshot;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.service.RecordService;
import com.gaudiy.orderbook.commons.utils.Utils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class OrderBookServiceImpl implements OrderBookService {

    private final Logger LOG = Logger.getLogger("OrderBookServiceImpl.class");

    @Value("${binance.depth.snapshot.uri}")
    private String depthSnapshotUri;

    @Value("${currency.process:BTC}")
    private String currency;

    @Autowired
    private RecordService recordService;

    @Override
    public void printOrderBook(Long orderBookId, String currency) throws IllegalArgumentException {
        try {
            var storage = Utils.retrieveStorage(currency);
            final var orderBooks = storage.getOrderBooks();

            var orderBooksFiltered = orderBooks
                    .stream()
                    .filter(ob -> ob.getLastUpdateId().equals( orderBookId))
                    .limit(2)
                    .collect(Collectors.toList());
            if (orderBooksFiltered.size() > 1) {
                throw new IllegalArgumentException("There are two orders with the same lastUpdateId..");
            }

            final OrderBook orderToProcess = orderBooksFiltered.size() != 0 ? orderBooksFiltered.get(0) : new OrderBook();
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
                final OrderBook orderToCompare = orderBooksFiltered.size() != 0 ? orderBooksFiltered.get(0) : new OrderBook();

                List<String> bidsList = new ArrayList<>();
                List<String> asksList = new ArrayList<>();

                bidsList.add("\u001B[32mBuy");
                asksList.add("\u001B[31mSell");

                bidsList.addAll( bids
                        .keySet()
                        .stream()
                        .sorted()
                        .limit(50)
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
                        .limit(50)
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

                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= bids.size(); i++) {
                    sb.append(String.format("%-30.32s    %-30.32s%n", bidsList.get(i), asksList.get(i)));
                }

                var volumeDiffer = orderToProcess.getTotalVolume().subtract(orderToCompare.getTotalVolume());

                sb.append(
                        String.format(
                                "\u001B[33mTotal current %s volume variation: %s%n",
                                (currency + Constants.USDT).toUpperCase(),
                                volumeDiffer
                        )
                );
                System.out.println(sb);
                System.out.println("\u001B[0m");
            } else {
                System.out.println("System is retrieving orders, please wait..");
            }
        } catch (IllegalArgumentException e) {
            throw new OrderApplicationException(e, currency);
        } catch (IndexOutOfBoundsException e) {
            throw new OrderApplicationException(e, currency);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            throw new OrderApplicationException(e, currency);
        }

    }

    @Override
    public void orderBookUpdate(String currency) {
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(Utils.buildBinanceUri(currency, depthSnapshotUri, false)))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            Snapshot latestSnapshot = gson.fromJson(response.body(), Snapshot.class);

            recordService.orderUpdate(latestSnapshot.getLastUpdateId(), currency);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            //throw new OrderApplicationException(e, currency);
        }

    }

}
