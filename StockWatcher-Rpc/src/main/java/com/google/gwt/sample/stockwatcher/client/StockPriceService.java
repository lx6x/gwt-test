package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * TODO GWT RPC
 *
 * @author LJF
 * @version 1.0
 * @date 2021/04/12 16:59
 */
@RemoteServiceRelativePath("stockPrices")
public interface StockPriceService extends RemoteService {

    StockPrice[] getPrices(String[] symbols) throws DelistedException;
}
