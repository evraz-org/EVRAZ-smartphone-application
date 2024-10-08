package com.bitshares.bitshareswallet.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Created by lorne on 31/10/2017.
 */

@Dao
public interface BitsharesDao {
    String ASSET = "'EVRAZ'";

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlance(List<BitsharesAsset> bitsharesAssetList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMarketTicker(List<BitsharesMarketTicker> bitsharesMarketTickerList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssetObject(List<BitsharesAssetObject> bitsharesAssetObjectList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistoryObject(List<BitsharesOperationHistory> bitsharesOperationHistoryList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccountObject(List<BitsharesAccountObject> bitsharesAccountObjectList);

    @Query("select * from balance where type = 0 and currency = :strCurrency")
    LiveData<BitsharesAsset> queryTargetAvalaliableBalance(String strCurrency);

    @Query("select * from balance where type = 1")
    List<BitsharesAsset> queryOrderBalance();

    @Delete
    void deleteBalance(List<BitsharesAsset> bitsharesAssetList);

    @Delete
    void deleteOperationHistory(List<BitsharesOperationHistory> bitsharesOperationHistoryList);

    @Query("select balance.id as id, balance.currency as quote, ticker.base as base, " +
            "sum(CASE WHEN type = 1 THEN balance.amount ELSE 0 END) as orders, sum(balance.amount) as amount, sum(balance.amount * ticker.latest * BTS.precision / balance.precision) as total, balance.precision as quote_precision, " +
            "BTS.precision as base_precision, sum(balance.amount * ticker.latest / balance.precision * CURRENCY.precision * currency_ticker.latest) as balance, " +
            "CURRENCY.precision as currency_precision, currency_ticker.base as currency from balance " +
            "inner join (select * from market_ticker) as ticker on balance.currency = ticker.quote and ticker.base = " + ASSET +
            "inner join (select * from asset_object where symbol = " + ASSET + ") as BTS " +
            "inner join (select * from market_ticker) as currency_ticker on currency_ticker.quote = " + ASSET + " and currency_ticker.base = :currency " +
            "inner join (select * from asset_object where symbol = :currency) as CURRENCY on currency_ticker.base = CURRENCY.symbol group by balance.currency ")
    LiveData<List<BitsharesBalanceAsset>> queryBalance(String currency);

    @Query("select balance.id as id, balance.currency as quote, ticker.base as base, " +
            "sum(CASE WHEN type = 1 THEN balance.amount ELSE 0 END) as orders, sum(CASE WHEN type = 0 THEN balance.amount ELSE 0 END) as amount, sum(balance.amount * ticker.latest * BTS.precision / balance.precision) as total, balance.precision as quote_precision, " +
            "BTS.precision as base_precision, sum(balance.amount * ticker.latest / balance.precision * CURRENCY.precision * currency_ticker.latest) as balance, " +
            "CURRENCY.precision as currency_precision, currency_ticker.base as currency from balance " +
            "inner join (select * from market_ticker) as ticker on balance.currency = ticker.quote and ticker.base = " + ASSET +
            "inner join (select * from asset_object where symbol = " + ASSET + ") as BTS " +
            "inner join (select * from market_ticker) as currency_ticker on currency_ticker.quote = " + ASSET + " and currency_ticker.base = :currency " +
            "inner join (select * from asset_object where symbol = :currency) as CURRENCY on currency_ticker.base = CURRENCY.symbol group by balance.currency ")
    LiveData<List<BitsharesBalanceAsset>> queryAvaliableBalances(String currency);

    @Query("select * from operation_history order by timestamp desc")
    LiveData<List<BitsharesOperationHistory>> queryOperationHistory();

    @Query("select history_id from operation_history order by timestamp desc limit 1")
    String queryOperationHistoryLatestId();

    @Query("select * from account_object")
    LiveData<List<BitsharesAccountObject>> queryAccountObject();

    @Query("select * from asset_object")
    LiveData<List<BitsharesAssetObject>> queryAssetObjectData();

    @Query("select * from asset_object where symbol in (:symbolArray)")
    List<BitsharesAssetObject> queryAssetObject(String[] symbolArray);

    @Query("select * from asset_object")
    List<BitsharesAssetObject> queryAssetObject();

    @Query("select * from balance")
    List<BitsharesAsset> queryBalanceList();

    @Query("select * from operation_history")
    List<BitsharesOperationHistory> queryOperationHistoryList();

    @Query("select * from asset_object where asset_id = :strAssetId ")
    BitsharesAssetObject queryAssetObjectById(String strAssetId);

    @Query("select * from market_ticker")
    LiveData<List<BitsharesMarketTicker>> queryMarketTicker();

    @Query("select * from market_ticker where base = :base and quote = :quote")
    LiveData<BitsharesMarketTicker> queryMarketTicker(String base, String quote);


}
