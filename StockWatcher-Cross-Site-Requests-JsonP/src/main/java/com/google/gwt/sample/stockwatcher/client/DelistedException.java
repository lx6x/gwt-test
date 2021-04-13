package com.google.gwt.sample.stockwatcher.client;

import java.io.Serializable;

/**
 * TODO exception
 *
 * @author LJF
 * @version 1.0
 * @date 2021/04/12 17:21
 */
public class DelistedException extends Exception implements Serializable {

    private String symbol;

    public DelistedException() {
    }

    public DelistedException(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
