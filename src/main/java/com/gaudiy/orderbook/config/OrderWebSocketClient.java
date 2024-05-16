package com.gaudiy.orderbook.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaudiy.orderbook.service.RecordService;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.nio.ByteBuffer;

public class OrderWebSocketClient extends WebSocketClient {

    @Autowired
    private RecordService recordService;

    private final String currency;

    public OrderWebSocketClient(URI serverURI, String currency) {
        super(serverURI);
        this.currency = currency;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.printf("Closed with exit code %s, additional info: %s%n", code, reason);
    }

    @Override
    public void onMessage(String message) {
        recordService.manageNewRecordReceived(message, currency);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.printf("ByteBuffer received during connection : %s%n", message.toString());
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

}
