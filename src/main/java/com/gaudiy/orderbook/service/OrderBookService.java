package com.gaudiy.orderbook.service;

public interface OrderBookService {

    void printOrderBook(Long orderBookId, String currency) throws IllegalArgumentException;

    void orderBookUpdate(String currency);

}
