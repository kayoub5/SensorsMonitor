package com.kaanich.sensorsmonitor.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kaanich.sensorsmonitor.DatabaseHelper;
import com.kaanich.sensorsmonitor.models.Measurement;
import com.kaanich.sensorsmonitor.reporters.Reporter;

import java.sql.SQLException;

public class StatusService extends IntentService {

    private DatabaseHelper databaseHelper;

    public StatusService() {
        super("StatusService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra("resultReceiver")) {
            ResultReceiver resultReceiver =  intent.getParcelableExtra("resultReceiver");
            Bundle result = new Bundle();
            result.putBoolean("capable.messaging", true);
            result.putBoolean("capable.calling", true);
            result.putBoolean("capable.calling.video", false);

            result.putBoolean("capable.connectivity", true);
            result.putBoolean("capable.connectivity.web", true);

            result.putBoolean("capable.media.streaming", true);
            result.putBoolean("capable.media.streaming.HD", false);

            result.putBoolean("capable.media.streaming.HD", false);

            String[] sensors = {Reporter.BATTERY_LEVEL, Reporter.GSM_SIGNAL_STRENGTH};
            for (String sensor: sensors) {
                Measurement measurement = lastMeasurement(sensor);
                if (measurement != null) {
                    result.putLong(sensor + ".value", measurement.getValue());
                }
            }
            resultReceiver.send(0, result);
        }
    }

    private Measurement lastMeasurement(String sensor) {
        try {
            Dao<Measurement,Long> measurementDao = databaseHelper.getDao(Measurement.class);
            QueryBuilder<Measurement, Long> queryBuilder = measurementDao.queryBuilder();
            queryBuilder.where().eq("sensor_id", sensor);
            queryBuilder.orderBy("timestamp", false);
            return queryBuilder.queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }

}