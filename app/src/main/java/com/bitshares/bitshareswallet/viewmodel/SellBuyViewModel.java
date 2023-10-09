package com.bitshares.bitshareswallet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bitshares.bitshareswallet.BitsharesApplication;
import com.bitshares.bitshareswallet.livedata.StatusChangeLiveData;
import com.bitshares.bitshareswallet.repository.AvailableBalanceRepository;
import com.bitshares.bitshareswallet.room.BitsharesAsset;
import com.bitshares.bitshareswallet.room.BitsharesBalanceAsset;
import com.bitshares.bitshareswallet.room.BitsharesDao;
import com.bituniverse.network.Resource;

import java.util.List;

/**
 * Created by lorne on 02/11/2017.
 */

public class SellBuyViewModel extends ViewModel {
    private MutableLiveData<String> currencyData = new MutableLiveData<>();
    private StatusChangeLiveData statusChangeLiveData = new StatusChangeLiveData();

    private BitsharesDao bitsharesDao;
    private LiveData<List<BitsharesBalanceAsset>> balancesList;

    public SellBuyViewModel() {
        bitsharesDao = BitsharesApplication.getInstance().getBitsharesDatabase().getBitsharesDao();
        balancesList = bitsharesDao.queryAvaliableBalances("USD");
    }

    public void changeBalanceAsset(String currency) {
        currencyData.setValue(currency);
    }

    public LiveData<Resource<BitsharesAsset>> getAvaliableBalance() {
        LiveData<Resource<BitsharesAsset>> balanceData = Transformations.switchMap(
                Transformations.switchMap(currencyData, input -> {
                    return statusChangeLiveData;
                }),
                statusChange -> {
                    return new AvailableBalanceRepository().getTargetAvaliableBlance(currencyData.getValue());
                });


        return balanceData;
    }

    public LiveData<List<BitsharesBalanceAsset>> getBalancesList() {
        return balancesList;
    }
}
