package com.gaudiy.orderbook.controller;

import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    @Autowired
    private WebSocketClient webSocketClient;

    @GetMapping(path = "/conn")
    public String webSocketController() {
        try {
            System.out.println("will start connection");
            webSocketClient.connect();
            return "Success";
        } catch (Exception e) {
            return "Failed";
        }

    }

}
