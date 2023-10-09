package com.good.code.starts.here.pairs;

import androidx.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bitshares.bitshareswallet.BitsharesApplication;
import com.bitshares.bitshareswallet.viewmodel.QuotationViewModel;

import org.evrazcoin.evrazwallet.R;

public class PairsFragment extends Fragment {

    public static PairsFragment newInstance() {
        return new PairsFragment();
    }

    private PairsRecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddPair;

    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.pairs_fragment, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(BitsharesApplication.getInstance());

        recyclerView = fragmentView.findViewById(R.id.recyclerView);
        fabAddPair = fragmentView.findViewById(R.id.addServer);

        fabAddPair.setOnClickListener(view -> {
            View dialogView = inflater.inflate(R.layout.dialog_add_pair, null);
            EditText first = dialogView.findViewById(R.id.editText);
            EditText second = dialogView.findViewById(R.id.editText2);

            new AlertDialog.Builder(PairsFragment.this.getActivity())
                    .setTitle(R.string.add_pair)
                    .setView(dialogView)
                    .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                        String firstStr = first.getText().toString();
                        String secondStr = second.getText().toString();
                        if(!check(firstStr) || !check(secondStr) || first.equals(second)) {
                            Toast.makeText(getActivity(), R.string.add_pair_err, Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.add(firstStr.toUpperCase() + ":" + secondStr.toUpperCase());
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
        });

        adapter = new PairsRecyclerAdapter(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);

        return fragmentView;
    }

    private boolean check(String name) {
        if(name.length() == 0) return false;
        if(name.charAt(0) == '.' || name.charAt(name.length()-1) == '.') return false;
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c) && c != '.') {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        QuotationViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuotationViewModel.class);
        viewModel.getMarketTicker().observe(getActivity(),listResource -> {});
        super.onDestroyView();
    }
}
