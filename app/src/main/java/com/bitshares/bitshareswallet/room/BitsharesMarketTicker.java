package com.bitshares.bitshareswallet.room;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.bitshares.bitshareswallet.market.MarketTicker;

/**
 * Created by lorne on 31/10/2017.
 */

@Entity(tableName = "market_ticker",
        indices = {@Index(value = {"base", "quote"}, unique = true)})
public class BitsharesMarketTicker {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @Embedded
    public MarketTicker marketTicker;
}
