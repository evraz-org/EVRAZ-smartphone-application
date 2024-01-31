package com.ngse.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.bitshares.bitshareswallet.BitsharesApplication;
import com.bitshares.bitshareswallet.market.MarketTicker;
import com.bitshares.bitshareswallet.room.BitsharesMarketTicker;
import com.bitshares.bitshareswallet.wallet.graphene.chain.utils;
import com.ngse.ui.NewMainActivity;
import com.ngse.utility.Utils;

import org.evrazcoin.evrazwallet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class QuotationCurrencyPairAdapter extends RecyclerView.Adapter<QuotationCurrencyPairAdapter.ViewHolder> {

    public static final String CURRENCY_PAIRS = "pairs";

    public interface OnItemClickListner {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
//        private View mViewSelected;
        private TextView mViewCurrencyPair;
        private TextView mViewPrice;
        private TextView mView24h;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
//            mViewSelected = mView.findViewById(R.id.viewSelected);
            mViewCurrencyPair = (TextView) mView.findViewById(R.id.textViewCurrencyPair);
            mViewPrice = (TextView) mView.findViewById(R.id.textViewPrice);
            mView24h = (TextView) mView.findViewById(R.id.textView24h);
        }
    }

    private String[] marrOptions;
    private String[] marrValues;
    private Context mContext;
    private OnItemClickListner monItemClickListner;
    private Map<String, Integer> mapSymbol2Id = new HashMap<>();
    private List<BitsharesMarketTicker> bitsharesMarketTickerList;
//    private Set<String> currecnyPairSet = new HashSet<>();
    private int selected = 0;

    public QuotationCurrencyPairAdapter(Context context) {
        mContext = context;
        marrOptions = context.getResources().getStringArray(R.array.quotation_currency_pair_options);
        marrValues = context.getResources().getStringArray(R.array.quotation_currency_pair_values);

        mapSymbol2Id.put("EVRAZ", R.mipmap.evraz);
        mapSymbol2Id.put("BTS", R.mipmap.bts);
        mapSymbol2Id.put("BTC", R.mipmap.btc);
        mapSymbol2Id.put("ETH", R.mipmap.eth);
        mapSymbol2Id.put("HERO", R.mipmap.hero);
        mapSymbol2Id.put("OBITS", R.mipmap.obits);
        mapSymbol2Id.put("SMOKE", R.mipmap.smok);
        mapSymbol2Id.put("USDT", R.mipmap.usdt);
        mapSymbol2Id.put("OCT", R.mipmap.oct);
        mapSymbol2Id.put("YOYOW", R.mipmap.yoyow);
        mapSymbol2Id.put("DASH", R.mipmap.dash);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_recyclerview_item_currency_pair, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        BitsharesMarketTicker bitsharesMarketTicker = bitsharesMarketTickerList.get(position);

      /*  if (selected == position) {
            holder.mViewSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mViewSelected.setVisibility(View.INVISIBLE);
        }*/

        MarketTicker marketTicker = bitsharesMarketTicker.marketTicker;
        String currencyPair = utils.getAssetSymbolDisply(marketTicker.quote) + " : " +
                utils.getAssetSymbolDisply(marketTicker.base);

        holder.mViewCurrencyPair.setText(currencyPair);

        holder.mViewPrice.setText(Utils.formatDecimal(marketTicker.latest));

        double percent_change = 0.f;
        try {
            percent_change = Double.parseDouble(marketTicker.percent_change);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String strPercentChange;
        if (percent_change >= 0) {
            if (!NewMainActivity.rasingColorRevers) {
                holder.mView24h.setBackgroundResource(R.drawable.oval_green_background);
            } else {
                holder.mView24h.setBackgroundResource(R.drawable.oval_red_background);
            }
            strPercentChange = String.format(
                    Locale.ENGLISH,
                    "+%.2f%%",
                    percent_change
            );
        } else {
            if (!NewMainActivity.rasingColorRevers) {
                holder.mView24h.setBackgroundResource(R.drawable.oval_red_background);
            } else {
                holder.mView24h.setBackgroundResource(R.drawable.oval_green_background);
            }
            strPercentChange = String.format(
                    Locale.ENGLISH,
                    "%.2f%%",
                    percent_change
            );
        }
        holder.mView24h.setText(strPercentChange);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monItemClickListner != null) {
                    selected = position;
                    monItemClickListner.onItemClick(holder.mView, position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (bitsharesMarketTickerList == null) {
            return 0;
        } else {
            return bitsharesMarketTickerList.size();
        }
    }

    public void setOnItemClickListenr(OnItemClickListner onItemClickListenr) {
        monItemClickListner = onItemClickListenr;
    }

    public void notifyDataUpdated(List<BitsharesMarketTicker> marketTickerList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BitsharesApplication.getInstance());
        List<String> currecnyPairSet = new ArrayList<>(prefs.getStringSet(CURRENCY_PAIRS, new HashSet<>()));
//        currecnyPairSet.addAll(arrValues);
        bitsharesMarketTickerList = new ArrayList<>();
        for (BitsharesMarketTicker bitsharesMarketTicker : marketTickerList) {
            if (currecnyPairSet.contains(bitsharesMarketTicker.marketTicker.quote + ":" + bitsharesMarketTicker.marketTicker.base)) {
                bitsharesMarketTickerList.add(bitsharesMarketTicker);
            }
        }

        Collections.sort(
                bitsharesMarketTickerList,
                (o1, o2) -> o1.marketTicker.quote.compareTo(o2.marketTicker.quote)
        );

        String strAssetPairConfig = prefs.getString("quotation_currency_pair", "BTS:USD");
        for (int i = 0; i < bitsharesMarketTickerList.size(); ++i) {
            MarketTicker marketTicker = bitsharesMarketTickerList.get(i).marketTicker;
            String currencyPair = utils.getAssetSymbolDisply(marketTicker.quote) + ":" +
                    utils.getAssetSymbolDisply(marketTicker.base);
            if (strAssetPairConfig.compareTo(currencyPair) == 0) {
                selected = i;
                break;
            }
        }

        notifyDataSetChanged();
    }

    public BitsharesMarketTicker getSelectedMarketTicker() {
        if (selected >= bitsharesMarketTickerList.size()) {
            return null;
        }

        return bitsharesMarketTickerList.get(selected);
    }

    public void addCurrencyPair(String pairStr) {
       SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(BitsharesApplication.getInstance());
        Set<String> pars = prefs.getStringSet(CURRENCY_PAIRS, new HashSet<>());
        Set<String> temp = new HashSet<>(pars);
        if(temp.add(pairStr)) {
            prefs.edit().putStringSet(CURRENCY_PAIRS, temp).apply();
        } else {
            Toast.makeText(BitsharesApplication.getInstance(), R.string.add_pair_exist, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeCurrencyPair(int pos) {
        BitsharesMarketTicker bitsharesMarketTicker = bitsharesMarketTickerList.get(pos);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BitsharesApplication.getInstance());
        Set<String> pars = prefs.getStringSet(CURRENCY_PAIRS, new HashSet<>());
        Set<String> temp = new HashSet<>(pars);
        temp.remove(bitsharesMarketTicker.marketTicker.quote + ":" + bitsharesMarketTicker.marketTicker.base);
        prefs.edit().putStringSet(CURRENCY_PAIRS, temp).apply();

        bitsharesMarketTickerList.remove(pos);
        notifyItemRemoved(pos);
    }
}
