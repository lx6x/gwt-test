package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * TODO StockData类将是覆盖现有JavaScript(StockPrice)对象的覆盖类型
 *
 * @author LJF
 * @version 1.0
 * @date 2021/04/12 18:56
 */
public class StockData extends JavaScriptObject {

    // Overlay types always have protected, zero argument constructors.
    protected StockData() {
    }

    // JSNI methods to get stock data.
    public final native String getSymbol() /*-{ return this.symbol; }-*/; // (3)
    public final native double getPrice() /*-{ return this.price; }-*/;
    public final native double getChange() /*-{ return this.change; }-*/;

    // Non-JSNI method to return change percentage.                       // (4)
    public final double getChangePercent() {
        return 100.0 * getChange() / getPrice();
    }

}
