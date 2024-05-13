package com.gaudiy.orderbook.scheduler;

import com.gaudiy.orderbook.entity.Snapshot;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.service.RecordService;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class Schedulers {

    @Autowired
    private RecordService recordService;

    @Autowired
    private WebSocketClient webSocketClient;

    @Autowired
    private OrderBookService orderBookService;

    @Value("${binance.depth.snapshot.uri}")
    private String depthSnapshotUri;

    @Scheduled(fixedDelay = 10000)
    public void scheduledBTCOrderBook() {

        if (!webSocketClient.isOpen()) {
            webSocketClient.connect();
        }

        System.out.println("Start scheduler for BTC order book printing");
        final var storage = BTCRecordsStorage.getInstance();
        final var recordList = storage.getRecordList();

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

            long start = System.currentTimeMillis();

            storage.saveOrderBookAndResetPriceLevels();
            recordList
                    .forEach(recordService::parseRecordPrices);

            orderBookService.printOrderBook();

            storage.cleanRecordList(latestSnapshot.getLastUpdateId());

            long finish = System.currentTimeMillis();
            System.out.println("Elapsed time: " + (finish -start));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

    }

    @Scheduled(fixedDelay = 10000)
    public void scheduledETHOrderBook() {

    }
}
