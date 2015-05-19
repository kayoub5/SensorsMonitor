package com.kaanich.sensorsmonitor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.kaanich.sensorsmonitor.services.MonitorService;
import com.kaanich.sensorsmonitor.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, MonitorService.class));

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }


    public static class GeneralPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            findPreference("debug_database").setOnPreferenceClickListener(this);
            findPreference("debug_status").setOnPreferenceClickListener(this);

        }
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals("debug_database")) {
                startActivity(new Intent(getActivity(), AndroidDatabaseManager.class));
                return true;
            }
            if (key.equals("debug_status")) {
                startActivity(new Intent(getActivity(), StatusActivity.class));
                return true;
            }
            return false;
        }
    }

}
