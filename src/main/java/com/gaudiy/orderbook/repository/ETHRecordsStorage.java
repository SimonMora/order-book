package com.gaudiy.orderbook.repository;

public final class ETHRecordsStorage extends Storage {

    private static ETHRecordsStorage INSTANCE;

    private ETHRecordsStorage() {
        super();
    }

    public static ETHRecordsStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ETHRecordsStorage();
        }
        return INSTANCE;
    }

}
