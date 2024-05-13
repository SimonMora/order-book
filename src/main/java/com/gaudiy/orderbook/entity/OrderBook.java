package com.gaudiy.orderbook.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class OrderBook implements Serializable {

    private BigDecimal totalVolume;
    private Map<String, String> bids;
    private Map<String, String>  asks;

    public OrderBook(Map<String, String>  bids, Map<String, String>  asks) {
        this.bids = bids;
        this.asks = asks;

        var bidsTotal = bids
                .keySet()
                .stream()
                .map(key -> {
                  final BigDecimal price = BigDecimal.valueOf(Double.parseDouble(key));
                  final BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(bids.get(key)));
                  return price.multiply(quantity);
                })
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

        var asksTotal = asks
                .keySet()
                .stream()
                .map(key -> {
                    final BigDecimal price = BigDecimal.valueOf(Double.parseDouble(key));
                    final BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(asks.get(key)));
                    return price.multiply(quantity);
                })
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);

        this.totalVolume = bidsTotal.add(asksTotal);
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Map<String, String> getBids() {
        return bids;
    }

    public void setBids(Map<String, String> bids) {
        this.bids = bids;
    }

    public Map<String, String> getAsks() {
        return asks;
    }

    public void setAsks(Map<String, String> asks) {
        this.asks = asks;
    }
}
