package com.bitshares.bitshareswallet.room;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.bitshares.bitshareswallet.wallet.graphene.chain.asset_object;
import com.bitshares.bitshareswallet.wallet.graphene.chain.object_id;

/**
 * Created by lorne on 31/10/2017.
 */

@Entity(tableName = "balance")
public class BitsharesAsset {
    public static final int TYPE_AVALIABLE = 0;
    public static final int TYPE_SELL_ORDER = 1;

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long amount;
    public String currency;
    public object_id<asset_object> asset_id;
    public int type;
    public long precision;
}
