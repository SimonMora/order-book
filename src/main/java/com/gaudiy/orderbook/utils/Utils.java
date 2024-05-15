package com.gaudiy.orderbook.utils;

import com.gaudiy.orderbook.repository.BTCRecordsStorage;
import com.gaudiy.orderbook.repository.ETHRecordsStorage;
import com.gaudiy.orderbook.repository.Storage;

public class Utils {

    public static String buildBinanceUri(String currency, String uri, Boolean lower) {
        final String tradingPair = currency + Constants.USDT;
        return uri.replace(Constants.SYMBOL_REPLACEMENT, lower ? tradingPair.toLowerCase() : tradingPair.toUpperCase());
    }

    public static Storage retrieveStorage(String currency) {
        return currency.equals( Constants.CURRENCY_BTC ) ? BTCRecordsStorage.getInstance() : ETHRecordsStorage.getInstance();
    }

}
