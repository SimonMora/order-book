package com.gaudiy.orderbook.service;

public interface OrderBookService {

    void printOrderBook(Long orderBookId) throws IllegalArgumentException;

    void orderBookUpdate();

}
