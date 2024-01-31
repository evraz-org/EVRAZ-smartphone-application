package com.ngse.ui.main.balanceitems;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitshares.bitshareswallet.BaseFragment;
import com.bitshares.bitshareswallet.room.BitsharesBalanceAsset;
import com.bitshares.bitshareswallet.viewmodel.WalletViewModel;
import com.google.android.material.tabs.TabLayout;
import com.ngse.utility.Utils;

import org.evrazcoin.evrazwallet.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;


public class PortfolioFragment extends BaseFragment {

    private static final String PORTFOLIO_PREFERENCES = "PORTFOLIO_PREFERENCES";
    private static final String FILTER_PORTFOLIO = "filterPortfolio";

    public static final String TAG = PortfolioFragment.class.getName();
    private BalancesAdapter mBalancesAdapter;

    private OnFragmentInteractionListener mListener;
    private MenuItem backMenuItem;

    private TabLayout tabs;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        backMenuItem = menu.add(R.string.back)
                .setIcon(R.drawable.abc_ic_ab_back_material)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == backMenuItem)
            getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public static PortfolioFragment newInstance() {
        return new PortfolioFragment();
    }


    @Override
    public void onResume() {
        super.onResume();

        updateData();
    }

    private void updateData() {
        WalletViewModel walletViewModel = ViewModelProviders.of(getActivity()).get(WalletViewModel.class);
        walletViewModel.getBalanceData().observe(
                this, resourceBalanceList -> {
                    switch (resourceBalanceList.status) {
                        case SUCCESS:
                            processShowdata(resourceBalanceList.data);
                            mBalancesAdapter.notifyBalancesDataChanged(resourceBalanceList.data);
                            break;
                        case LOADING:
                            if (resourceBalanceList.data != null) {
                                mBalancesAdapter.notifyBalancesDataChanged(resourceBalanceList.data);
                            }
                            break;
                    }
                });
    }

    @Override
    public void onShow() {
        super.onShow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_portfolio, container, false);
        tabs = view.findViewById(R.id.tabs);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBalancesAdapter = new BalancesAdapter();
        mBalancesAdapter.tabIndex = tabs.getSelectedTabPosition();
        mBalancesAdapter.filterPortfolio = strSetToIntSet(getContext().getSharedPreferences(PORTFOLIO_PREFERENCES, MODE_PRIVATE).getStringSet(FILTER_PORTFOLIO, new HashSet<>()));

        recyclerView.setAdapter(mBalancesAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                int id = mBalancesAdapter.bitsharesBalanceAssetList.get(pos).hashCode();

                if(tabs.getSelectedTabPosition() == 0) {
                    mBalancesAdapter.filterPortfolio.add(id);
                } else {
                    mBalancesAdapter.filterPortfolio.remove(id);
                }

                mBalancesAdapter.bitsharesBalanceAssetList.remove(pos);
                mBalancesAdapter.notifyItemRemoved(pos);
            }
        };

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBalancesAdapter.tabIndex = tabs.getSelectedTabPosition();
                updateData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        ButterKnife.bind(this, view);
        return view;
    }

    void processShowdata(List<BitsharesBalanceAsset> bitsharesBalanceAssetList) {
//        long totalBTS = 0;
        long totalBalance = 0;
        for (BitsharesBalanceAsset bitsharesBalanceAsset : bitsharesBalanceAssetList) {
//            totalBTS += bitsharesBalanceAsset.total;
            totalBalance += bitsharesBalanceAsset.balance;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        getContext().getSharedPreferences(PORTFOLIO_PREFERENCES, MODE_PRIVATE)
                .edit()
                .putStringSet(FILTER_PORTFOLIO, intSetToStrSet(mBalancesAdapter.filterPortfolio))
                .apply();

        mListener = null;
    }

    private Set<Integer> strSetToIntSet(Set<String> stringSet) {
        Set<Integer> result = new HashSet<>();
        for (String item : stringSet) {
            result.add(Integer.valueOf(item));
        }
        return result;
    }

    private Set<String> intSetToStrSet(Set<Integer> integerSet) {
        Set<String> result = new HashSet<>(integerSet.size());
        for(Integer integer : integerSet) {
            result.add(integer.toString());
        }
        return result;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void notifyUpdate() {

    }

    class BalanceItemViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView viewUnit;
        public TextView viewAmount;
        public TextView viewOrders;
        public TextView viewAvailable;

        public BalanceItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            viewAmount = (TextView) itemView.findViewById(R.id.textViewNumber);
            viewUnit = (TextView) itemView.findViewById(R.id.textViewUnit);
//            viewEqual = (TextView) itemView.findViewById(R.id.textViewEqual);
            viewOrders = (TextView) itemView.findViewById(R.id.textExchangeRate);
            viewAvailable = (TextView) itemView.findViewById(R.id.textUSDBalance);
        }
    }

    class BalancesAdapter extends RecyclerView.Adapter<BalanceItemViewHolder> {
        private List<BitsharesBalanceAsset> bitsharesBalanceAssetList;
        Set<Integer> filterPortfolio = new HashSet<>();
        int tabIndex = 0;

        private List<BitsharesBalanceAsset> filter(List<BitsharesBalanceAsset> balanceAssets) {

            List<BitsharesBalanceAsset> result = new ArrayList<>();
            for (BitsharesBalanceAsset item : balanceAssets) {
                if (tabIndex == 0) {
                    if (!filterPortfolio.contains(item.hashCode())) {
                        result.add(item);
                    }
                } else {
                    if (filterPortfolio.contains(item.hashCode())) {
                        result.add(item);
                    }
                }
            }
            return result;
        }

        @Override
        public BalanceItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_recyclerview_item_balances, parent, false);
            return new BalanceItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BalanceItemViewHolder holder, int position) {
            BitsharesBalanceAsset bitsharesBalanceAsset = bitsharesBalanceAssetList.get(position);
            float balance = (float) bitsharesBalanceAsset.amount / bitsharesBalanceAsset.quote_precision;
            float orders = (float) bitsharesBalanceAsset.orders / bitsharesBalanceAsset.quote_precision;
            float available = balance  -  orders;

            holder.viewUnit.setText(bitsharesBalanceAsset.quote);
            holder.viewAmount.setText(Utils.formatDecimal(balance));
            holder.viewOrders.setText(Utils.formatDecimal(orders));
            holder.viewAvailable.setText(Utils.formatDecimal(available));
        }

        @Override
        public int getItemCount() {
            if (bitsharesBalanceAssetList == null) {
                return 0;
            } else {
                return bitsharesBalanceAssetList.size();
            }
        }

        public void notifyBalancesDataChanged(List<BitsharesBalanceAsset> bitsharesBalanceAssetList) {
            this.bitsharesBalanceAssetList = filter(bitsharesBalanceAssetList);

            notifyDataSetChanged();
        }
    }

}
