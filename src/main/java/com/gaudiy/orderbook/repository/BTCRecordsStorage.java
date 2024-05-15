package com.gaudiy.orderbook.repository;

public final class BTCRecordsStorage extends Storage {

    private static BTCRecordsStorage INSTANCE;

    private BTCRecordsStorage() {
        super();
    }
    public static BTCRecordsStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BTCRecordsStorage();
        }
        return INSTANCE;
    }

}
