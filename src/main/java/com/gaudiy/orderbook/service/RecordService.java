package com.gaudiy.orderbook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaudiy.orderbook.entity.Record;

public interface RecordService {

    void manageNewRecordReceived(String object, String currency) throws JsonProcessingException;

    void parseRecordPrices(Record record, String currency);

    void orderUpdate(Long lastUpdateId, String currency);
}
