package com.gaudiy.orderbook.repository;

public final class ETHRecordsStorage extends Storage {

    private static ETHRecordsStorage INSTANCE;

    private ETHRecordsStorage(String currency) {
        super(currency);
    }

    public static ETHRecordsStorage getInstance(String currency) {
        if (INSTANCE == null) {
            INSTANCE = new ETHRecordsStorage(currency);
        }
        return INSTANCE;
    }

}
