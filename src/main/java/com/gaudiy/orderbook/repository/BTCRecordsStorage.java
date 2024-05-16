package com.gaudiy.orderbook.repository;

public final class BTCRecordsStorage extends Storage {

    private static BTCRecordsStorage INSTANCE;

    private BTCRecordsStorage(String currency) {
        super(currency);
    }
    public static BTCRecordsStorage getInstance(String currency) {
        if (INSTANCE == null) {
            INSTANCE = new BTCRecordsStorage(currency);
        }
        return INSTANCE;
    }

}
