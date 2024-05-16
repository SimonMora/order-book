package com.gaudiy.orderbook.service;

import com.gaudiy.orderbook.entity.Record;

public interface RecordService {

    void manageNewRecordReceived(String object, String currency);

    void parseRecordPrices(Record record, String currency);

    void orderUpdate(Long lastUpdateId, String currency);
}
