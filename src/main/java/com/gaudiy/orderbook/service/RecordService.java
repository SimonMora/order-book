package com.gaudiy.orderbook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gaudiy.orderbook.entity.Record;

public interface RecordService {

    void manageNewRecordReceived(String object) throws JsonProcessingException;

    void parseRecordPrices(Record record);

    void orderUpdate(Long lastUpdateId);
}
