package com.kaanich.sensorsmonitor.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.kaanich.sensorsmonitor.DatabaseHelper;
import com.kaanich.sensorsmonitor.models.Measurement;
import com.kaanich.sensorsmonitor.reporters.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MonitorService extends Service implements
        RecordListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private DatabaseHelper databaseHelper;
    private List<Reporter> reporters;
    private List<Measurement> measurements;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_MULTI_PROCESS);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        measurements = new ArrayList<>();

        reporters = new ArrayList<>();
        reporters.add(new TelephonySignalStrengthReporter(this));
        reporters.add(new BatteryLevelReporter(this));

        for (Reporter reporter : reporters) {
            reporter.onCreate(this);
            boolean enabled = sharedPreferences.getBoolean(reporter.getName() + ".enabled", true);
            if (enabled) {
                reporter.onStart();
            }
        }

        timer = new Timer();
        timer.schedule(new SaveTask(), TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(1));
        timer.schedule(new CleanTask(), TimeUnit.MINUTES.toMillis(5), TimeUnit.HOURS.toMillis(1));

    }

    @Override
    public void onDestroy() {
        databaseHelper.close();
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("monitor.enabled")) {
            if (!sharedPreferences.getBoolean(key, true)) {
                stopSelf();
            }
        }
        for (Reporter reporter : reporters) {
            if (key.equals(reporter.getName() + ".enabled")) {
                boolean enabled = sharedPreferences.getBoolean(key, true);
                if (enabled) {
                    reporter.onStart();
                } else {
                    reporter.onStop();
                }
            }
        }
    }

    @Override
    public void onRecord(Measurement measurement) {
        measurements.add(measurement);
    }

    private class SaveTask extends TimerTask {
        @Override
        public void run() {
            try {
                final Dao<Measurement, Long> measurementDao = MonitorService.this.databaseHelper.getDao(Measurement.class);
                measurementDao.callBatchTasks(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        for (Measurement measurement : measurements) {
                            measurementDao.create(measurement);
                        }
                        measurements.clear();
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CleanTask extends TimerTask {
        @Override
        public void run() {
            try {
                final Dao<Measurement, Long> measurementDao = MonitorService.this.databaseHelper.getDao(Measurement.class);
                DeleteBuilder<Measurement, Long> deleteBuilder = measurementDao.deleteBuilder();

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -30);
                deleteBuilder.where().lt("timestamp", calendar.getTime());
                deleteBuilder.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
