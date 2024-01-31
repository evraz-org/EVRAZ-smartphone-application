package com.ngse.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.market.MarketTicker;
import com.bitshares.bitshareswallet.room.BitsharesMarketTicker;
import com.bitshares.bitshareswallet.viewmodel.QuotationViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ngse.ui.NewMainActivity;

import org.evrazcoin.evrazwallet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExchangeFragment extends BaseFragment {
    private static final String TAG = "QuotationFragment";

    private QuotationCurrencyPairAdapter quotationCurrencyPairAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    public ExchangeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ExchangeFragment newInstance() {
        ExchangeFragment fragment = new ExchangeFragment();
        return fragment;
    }

    @SuppressLint("MutatingSharedPrefs")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment_exchange, container, false);
        ButterKnife.bind(this, view);

        FloatingActionButton fabAddPair = view.findViewById(R.id.addExchange);

        quotationCurrencyPairAdapter = new QuotationCurrencyPairAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(quotationCurrencyPairAdapter);
        mRecyclerView.setItemAnimator(null);

        QuotationViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuotationViewModel.class);
        quotationCurrencyPairAdapter.setOnItemClickListenr(new QuotationCurrencyPairAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(View view, int position) {
                //mListener.notifyCurrencyPairChange();
                if (quotationCurrencyPairAdapter.getSelectedMarketTicker() != null) {
                    MarketTicker marketTicker = quotationCurrencyPairAdapter.getSelectedMarketTicker().marketTicker;
                    viewModel.selectedMarketTicker(new Pair(marketTicker.base, marketTicker.quote));
                    ((NewMainActivity) getActivity()).showTradingScheduleFragment();
                }

            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                quotationCurrencyPairAdapter.removeCurrencyPair(pos);
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        fabAddPair.setOnClickListener(button -> {
            View dialogView = inflater.inflate(R.layout.dialog_add_pair, null);
            EditText first = dialogView.findViewById(R.id.editText);
            EditText second = dialogView.findViewById(R.id.editText2);

            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.add_pair)
                    .setView(dialogView)
                    .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                        String firstStr = first.getText().toString();
                        String secondStr = second.getText().toString();
                        if(check(firstStr) || check(secondStr) || first.equals(second)) {
                            Toast.makeText(getActivity(), R.string.add_pair_err, Toast.LENGTH_SHORT).show();
                        } else {
                            quotationCurrencyPairAdapter.addCurrencyPair(firstStr.toUpperCase() + ":" + secondStr.toUpperCase());
                            updateData();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
        });

        return view;
    }


    @Override
    public void onShow() {
        super.onShow();

        updateData();
    }

    private void updateData() {
        QuotationViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuotationViewModel.class);
        viewModel.getMarketTicker().removeObservers(this);
        viewModel.getSelectedMarketTicker().removeObservers(this);

        viewModel.getMarketTicker().observe(
                this,
                marketTickerListResource -> {
                    switch (marketTickerListResource.status) {
                        case ERROR:
                            break;
                        case LOADING:
                            if (marketTickerListResource.data != null && marketTickerListResource.data.size() != 0) {
                                quotationCurrencyPairAdapter.notifyDataUpdated(marketTickerListResource.data);
                                BitsharesMarketTicker ticker = quotationCurrencyPairAdapter.getSelectedMarketTicker();
                                if(ticker != null) {
                                    MarketTicker marketTicker = ticker.marketTicker;
                                    viewModel.selectedMarketTicker(new Pair(marketTicker.base, marketTicker.quote));
                                }
                            }
                            break;
                        case SUCCESS:
                            quotationCurrencyPairAdapter.notifyDataUpdated(marketTickerListResource.data);
                            BitsharesMarketTicker ticker = quotationCurrencyPairAdapter.getSelectedMarketTicker();
                            if(ticker != null) {
                                MarketTicker marketTicker = ticker.marketTicker;
                                viewModel.selectedMarketTicker(new Pair(marketTicker.base, marketTicker.quote));
                            }
                            break;
                    }
                });


        viewModel.getSelectedMarketTicker().observe(this, currencyPair -> quotationCurrencyPairAdapter.notifyDataSetChanged());
    }

    @Override
    public void onHide() {
        super.onHide();
    }

    private boolean check(String name) {
        if(name.length() == 0) return true;
        if(name.charAt(0) == '.' || name.charAt(name.length()-1) == '.') return true;
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c) && c != '.') {
                return true;
            }
        }
        return false;
    }
}
