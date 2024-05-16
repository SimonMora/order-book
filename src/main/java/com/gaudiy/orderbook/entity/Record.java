package com.gaudiy.orderbook.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Record implements Serializable, Comparable<Record> {

    @SerializedName("e")
    private String event;

    @SerializedName("E")
    private Long eventTime;

    @SerializedName("s")
    private String symbol;

    @SerializedName("U")
    private Long initialUpdateId;

    @SerializedName("u")
    private Long finalUpdateId;

    @SerializedName("b")
    private List<List<String>> bids;

    @SerializedName("a")
    private List<List<String>> asks;

    public Record(String event, Long eventTime, String symbol,
                  Long initialUpdateId, Long finalUpdateId,
                  List<List<String>> bids, List<List<String>> asks
    ) {
        this.event = event;
        this.eventTime = eventTime;
        this.symbol = symbol;
        this.initialUpdateId = initialUpdateId;
        this.finalUpdateId = finalUpdateId;
        this.bids = bids;
        this.asks = asks;
    }

    public Record() {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getInitialUpdateId() {
        return initialUpdateId;
    }

    public void setInitialUpdateId(Long initialUpdateId) {
        this.initialUpdateId = initialUpdateId;
    }

    public Long getFinalUpdateId() {
        return finalUpdateId;
    }

    public void setFinalUpdateId(Long finalUpdateId) {
        this.finalUpdateId = finalUpdateId;
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

    @Override
    public String toString() {
        return "Record{" +
                "event='" + event + '\'' +
                ", eventTime=" + eventTime +
                ", symbol='" + symbol + '\'' +
                ", initialUpdateId=" + initialUpdateId +
                ", finalUpdateId=" + finalUpdateId +
                ", bids=" + bids +
                ", asks=" + asks +
                '}';
    }

    @Override
    public int compareTo(Record o) {
        return this.finalUpdateId.compareTo(o.getFinalUpdateId());
    }
}
