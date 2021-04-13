package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Entry point classes define
 */
public class StockWatcher implements EntryPoint {
    private static final int REFRESH_INTERVAL = 5000;
    private final VerticalPanel mainPanel = new VerticalPanel();
    private final FlexTable stocksFlexTable = new FlexTable();
    private final HorizontalPanel addPanel = new HorizontalPanel();
    private final TextBox newSymbolTextBox = new TextBox();
    private final Button addStockButton = new Button("Add");
    private final Label lastUpdatedLabel = new Label();
    private final ArrayList<String> stocks = new ArrayList<>();

    // 处理GET错误
    private Label errorMsgLabel = new Label();
    // success
    private static final String JSON_URL = GWT.getModuleBaseURL() + "stockPrices?q=";
    // error
//    private static final String JSON_URL = GWT.getModuleBaseURL() + "qq?q=";

    /**
     * 入口点方法
     */
    public void onModuleLoad() {

        mainPanel.addStyleName("mainPanel");

        // Create table for stock data.
        stocksFlexTable.setText(0, 0, "Symbol");
        stocksFlexTable.setText(0, 1, "Price");
        stocksFlexTable.setText(0, 2, "Change");
        stocksFlexTable.setText(0, 3, "Remove");

        // Add styles to elements in the stock list table.
        stocksFlexTable.setCellPadding(6);

        // Add styles to elements in the stock list table.
        stocksFlexTable.addStyleName("watchList");
        stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
        stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListNumericColumn");


        // Assemble Add Stock panel.
        addPanel.add(newSymbolTextBox);
        addPanel.add(addStockButton);
        addPanel.addStyleName("addPanel");

        // Assemble Main panel.
        errorMsgLabel.setStyleName("errorMessage");
        errorMsgLabel.setVisible(false);

        // Assemble Main panel.
        mainPanel.add(errorMsgLabel);
        mainPanel.add(stocksFlexTable);
        mainPanel.add(addPanel);
        mainPanel.add(lastUpdatedLabel);

        // Associate the Main panel with the HTML host page. 通过 Root 面板将 Main面板与宿主页面相关联。
        RootPanel.get("stockList").add(mainPanel);

        // Move cursor focus to the input box.  不太懂这是做什么的
        newSymbolTextBox.setFocus(true);

        // Setup timer to refresh list automatically.
        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                refreshWatchList();
            }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

        // Listen for mouse events on the Add button.
        addStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addStock();
            }
        });

        // Listen for keyboard events in the input box.
        newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                // Determine whether ENTER is triggered
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    addStock();
                }
            }
        });
    }

    /**
     * Add stock to FlexTable. Executed when the user clicks the addStockButton or
     * presses enter in the newSymbolTextBox.
     */
    private void addStock() {
        // Get text value
        String symbol = newSymbolTextBox.getText().toUpperCase().trim();

        // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
        if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
            Window.alert("'" + symbol + "' is not a valid symbol.");
            // Select the incorrectly typed text. addKeyDownHandler trigger
            newSymbolTextBox.selectAll();
            return;
        }

        // Text value replacement is empty
        newSymbolTextBox.setText("");

        // Don't add the stock if it's already in the table.
        if (stocks.contains(symbol)) {
            Window.alert("'" + symbol + "' can not repeat .");
            return;
        }

        // Add the stock to the table
        int row = stocksFlexTable.getRowCount();
        stocks.add(symbol);
        stocksFlexTable.setText(row, 0, symbol);
        stocksFlexTable.setWidget(row, 2, new Label());
        stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListNumericColumn");

        // Add a button to remove this stock from the table.
        Button removeStockButton = new Button("X");
        // removeStockButton.addStyleName("gwt-Button-remove");
        // 同上一个方法一样，这个为追加  真实实现 setStyleName(getStylePrimaryName() + '-' + styleSuffix, add);
        removeStockButton.addStyleDependentName("remove");
        removeStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // Gets the line number to delete
                int removedIndex = stocks.indexOf(symbol);
                stocks.remove(removedIndex);
                stocksFlexTable.removeRow(removedIndex + 1);
            }
        });
        stocksFlexTable.setWidget(row, 3, removeStockButton);

        // Get the stock price.
        refreshWatchList();


    }

    private void refreshWatchList() {
        // $100.00
        final double MAX_PRICE = 100.0;
        // +/- 2%
        final double MAX_PRICE_CHANGE = 0.02;

        StockPrice[] prices = new StockPrice[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            double price = Random.nextDouble() * MAX_PRICE;
            double change = price * MAX_PRICE_CHANGE * (Random.nextDouble() * 2.0 - 1.0);

            prices[i] = new StockPrice(stocks.get(i), price, change);
        }

        updateTable(prices);
    }

    /**
     * Update the Price and Change fields all the rows in the stock table.
     *
     * @param prices Stock data for all rows.
     */
    private void updateTable(StockPrice[] prices) {
        for (int i = 0; i < prices.length; i++) {
            updateTable(prices[i]);
        }

        // Display timestamp showing last refresh.
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        lastUpdatedLabel.setText("Last update :" + dateTimeFormat.format(new Date()));

        // Clear any errors.
        errorMsgLabel.setVisible(false);
    }

    /**
     * Update a single row in the stock table.
     *
     * @param stockData Stock data for a single row.
     */
    private void updateTable(StockPrice stockData) {
        // Make sure the stock is still in the stock table.
        if (!stocks.contains(stockData.getSymbol())) {
            return;
        }

        int row = stocks.indexOf(stockData.getSymbol()) + 1;

        // Format the data in the Price and Change fields.
        String priceText = NumberFormat.getFormat("#,##0.00").format(stockData.getPrice());
        NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
        String changeText = changeFormat.format(stockData.getChange());
        String changePercentText = changeFormat.format(stockData.getChangePercent());

        // Populate the Price and Change fields with new data.
        stocksFlexTable.setText(row, 1, priceText);
        Label changeWidget = (Label) stocksFlexTable.getWidget(row, 2);
        changeWidget.setText(changeText + " (" + changePercentText + "%)");

        // Change the color of text in the Change field based on its value.
        String changeStyleName = "noChange";
        if (stockData.getChangePercent() < -0.1f) {
            changeStyleName = "negativeChange";
        } else if (stockData.getChangePercent() > 0.1f) {
            changeStyleName = "positiveChange";
        }

        changeWidget.setStyleName(changeStyleName);
    }
}
