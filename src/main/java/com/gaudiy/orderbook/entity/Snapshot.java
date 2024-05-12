package com.gaudiy.orderbook.entity;

import java.io.Serializable;
import java.util.List;

public class Snapshot implements Serializable {

    private Long lastUpdateId;

    private List<List<String>> bids;

    private List<List<String>> asks;

    public Snapshot() {
    }

    public Long getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdatedId(Long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public List<List<String>> getBids() {
        return bids;
    }

    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }

    public List<List<String>> getAsks() {
        return asks;
    }

    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }
}
