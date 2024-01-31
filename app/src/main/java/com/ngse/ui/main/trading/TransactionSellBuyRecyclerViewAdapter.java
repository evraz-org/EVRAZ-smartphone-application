package com.ngse.ui.main.trading;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitshares.bitshareswallet.market.Order;

import org.evrazcoin.evrazwallet.R;

import java.util.List;

public class TransactionSellBuyRecyclerViewAdapter extends RecyclerView.Adapter<TransactionSellBuyRecyclerViewAdapter.ViewHolder> {
    public static final int ORDERITEMHEIGHTDP = 18;
    private List<Order> list;
    private boolean reversePosition = false;

    enum Type {
        BUY,
        SELL
    }

    private Type mType;

    public TransactionSellBuyRecyclerViewAdapter(Type type) {
        mType = type;
    }

    public void setList(List<Order> list) {
        if (getItemCount() > 0) {
            notifyItemRangeRemoved(0, getItemCount());
        }
        this.list = list;
        notifyItemRangeInserted(0, getItemCount());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_view_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.update(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public Order item;
        public TextView priceTextView;
        public TextView btsTextView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            priceTextView = (TextView) view.findViewById(R.id.price_text);
            btsTextView = (TextView) view.findViewById(R.id.bts_text);

            switch (mType) {
                case BUY:
                    priceTextView.setTextColor(view.getContext().getResources().getColor(R.color.quotation_top_green));
                    break;

                case SELL:
                    btsTextView.setTextColor(view.getContext().getResources().getColor(R.color.red));
                    break;
            }
        }

        public void update(Order order) {
            priceTextView.setText(!reversePosition ? String.format("%.5f", order.price) : String.format("%.5f", order.quote));
            btsTextView.setText(!reversePosition ? String.format("%.5f", order.quote) : String.format("%.5f", order.price));
        }
    }

    public void setReversePosition(boolean reversePosition) {
        this.reversePosition = reversePosition;
    }
}
