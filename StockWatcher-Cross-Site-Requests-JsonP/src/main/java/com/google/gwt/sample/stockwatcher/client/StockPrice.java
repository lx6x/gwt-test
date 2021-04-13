package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * TODO 封装股价数据
 *
 * @author LJF
 * @version 1.0
 * @date 2021/04/12 14:32
 */
public class StockPrice implements IsSerializable {

    private String symbol;
    private double price;
    private double change;

    public StockPrice(String symbol, double price, double change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

    public StockPrice() {
    }

    public String getSymbol() {
        return this.symbol;
    }

    public double getPrice() {
        return this.price;
    }

    public double getChange() {
        return this.change;
    }

    public double getChangePercent() {
        return this.change;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setChange(double change) {
        this.change = change;
    }
}
