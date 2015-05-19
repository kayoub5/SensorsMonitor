package com.kaanich.sensorsmonitor.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StatusActivity extends ListActivity {


    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        setListAdapter(adapter);

        Intent fetchCapabilities = new Intent("com.kaanich.sensorsmonitor.services.StatusService");
        fetchCapabilities.putExtra("resultReceiver", new StatusResultReceiver());
        startService(fetchCapabilities);

    }

    public class StatusResultReceiver extends ResultReceiver {
        public StatusResultReceiver() {
            super(null);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData != null) {
                final Bundle bundle = resultData;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> keys = new ArrayList<>(bundle.keySet());
                        Collections.sort(keys);
                        adapter.clear();
                        for (String key : keys) {
                            adapter.add(key + "=" + bundle.get(key));
                        }
                    }
                });
            }
        }
    }
}
