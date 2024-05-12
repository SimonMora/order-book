package com.gaudiy.orderbook.scheduler;

import com.gaudiy.orderbook.entity.Snapshot;
import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.service.OrderBookService;
import com.gaudiy.orderbook.service.RecordService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
//@RestController
public class Schedulers {

    @Autowired
    private RecordService recordService;

    @Autowired
    private OrderBookService orderBookService;

    @Value("${binance.depth.snapshot.uri}")
    private String depthSnapshotUri;

    private HttpClient httpClient;

    @Scheduled(fixedDelay = 10000)
    //@GetMapping(path = "/scheduler")
    public void scheduledBTCOrderBook() {
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

            var filteredList = recordList
                    .stream()
                    .filter(record -> record.getFinalUpdateId() < latestSnapshot.getLastUpdateId())
                    .collect(Collectors.toSet());

            filteredList.forEach(recordService::parseRecordPrices);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        orderBookService.printOrderBook();
    }

    @Scheduled(fixedDelay = 10000)
    public void scheduledETHOrderBook() {

    }
}