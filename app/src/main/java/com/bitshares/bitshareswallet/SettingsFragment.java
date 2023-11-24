package com.bitshares.bitshareswallet;


import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.good.code.starts.here.pairs.PairsFragment;
import com.good.code.starts.here.servers.ServersFragment;

import org.evrazcoin.evrazwallet.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference pairSelectPreference = findPreference("quotation_currency_pair");
        pairSelectPreference.setOnPreferenceClickListener(p -> {

            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, PairsFragment.newInstance()).addToBackStack(null).commit();
            return true;
        });

        Preference serverSelectPreference = findPreference("full_node_api_server");
        serverSelectPreference.setOnPreferenceClickListener(p -> {
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ServersFragment.newInstance()).addToBackStack(null).commit();
            return true;
        });
    }
}
