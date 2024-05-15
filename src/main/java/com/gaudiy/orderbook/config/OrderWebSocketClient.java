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
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        try {
            recordService.manageNewRecordReceived(message, currency);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

}
