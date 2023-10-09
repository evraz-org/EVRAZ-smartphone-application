package com.bitshares.bitshareswallet.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Created by lorne on 31/10/2017.
 */

@Database(entities = {
        BitsharesAsset.class,
        BitsharesMarketTicker.class,
        BitsharesAssetObject.class,
        BitsharesOperationHistory.class,
        BitsharesAccountObject.class
        }, version = 2)
@TypeConverters({RoomConverters.class})
public abstract class BitsharesDatabase extends RoomDatabase {
    public abstract BitsharesDao getBitsharesDao();
}
