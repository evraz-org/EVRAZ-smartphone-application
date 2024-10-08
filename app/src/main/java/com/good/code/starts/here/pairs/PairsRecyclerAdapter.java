package com.good.code.starts.here.pairs;
import static com.ngse.ui.main.QuotationCurrencyPairAdapter.CURRENCY_PAIRS;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import org.evrazcoin.evrazwallet.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PairsRecyclerAdapter extends RecyclerView.Adapter<PairsRecyclerAdapter.ViewHolder> {

    private int lastSelected;
    private int selected;

    private Context context;
    private SharedPreferences preferences;
    private List<String> pairs;

    private boolean onBind;

    public PairsRecyclerAdapter(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        pairs = new ArrayList<>(preferences.getStringSet(CURRENCY_PAIRS, new HashSet<>()));
        selected = pairs.indexOf(preferences.getString("quotation_currency_pair", "BTS:USD"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pair, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String[] pair = pairs.get(i).split(":");
        if(pair.length == 2) {
            viewHolder.first.setText(pair[0]);
            viewHolder.second.setText(pair[1]);
        }

        viewHolder.delete.setOnClickListener(v -> {
            if(viewHolder.getAdapterPosition() == selected) {
                Toast.makeText(context, R.string.cannot_delete_selected_pair, Toast.LENGTH_SHORT).show();
            } else {
                String currPair = pairs.get(viewHolder.getAdapterPosition());
                pairs.remove(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
                Set<String> temp = preferences.getStringSet(CURRENCY_PAIRS, new HashSet<>());
                temp.remove(currPair);
                preferences.edit().putStringSet(CURRENCY_PAIRS, temp).apply();
            }
        });

        onBind = false;
    }

    @Override
    public int getItemCount() {
        return pairs.size();
    }

    public void add(String pairStr) {
        Set<String> temp = preferences.getStringSet(CURRENCY_PAIRS, new HashSet<>());
        if(temp.add(pairStr)) {
            pairs.add(pairStr);
            preferences.edit().putStringSet(CURRENCY_PAIRS, temp).apply();
        } else {
            Toast.makeText(context, R.string.add_pair_exist, Toast.LENGTH_SHORT).show();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView first;
        TextView second;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            first = itemView.findViewById(R.id.first);
            second = itemView.findViewById(R.id.second);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
