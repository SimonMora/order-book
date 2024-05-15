package com.gaudiy.orderbook.utils;

public class Utils {

    public static String buildBinanceUri(String currency, String uri, Boolean lower) {
        final String tradingPair = currency + Constants.USDT;
        return uri.replace(Constants.SYMBOL_REPLACEMENT, lower ? tradingPair.toLowerCase() : tradingPair.toUpperCase());
    }

}
