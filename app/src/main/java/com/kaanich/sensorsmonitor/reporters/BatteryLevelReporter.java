package com.kaanich.sensorsmonitor.reporters;

import android.content.*;
import android.os.BatteryManager;
import com.kaanich.sensorsmonitor.models.*;

import lombok.Getter;

public class BatteryLevelReporter extends BroadcastReceiver implements Reporter {

    private int lastRecordedLevel = -1;
    private RecordListener recordListener;
    private Context context;
    @Getter public String name = BATTERY_LEVEL;

    public static Sensor SENSOR = new Sensor(BATTERY_LEVEL, Metric.PERCENTAGE);

    public BatteryLevelReporter(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    @Override
    public void onStart() {
        context.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onStop() {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        if (scale == -1 || level == -1) {
            return;
        }
        level = (level * 100) / scale;
        if (lastRecordedLevel == level) {
            return;
        }
        lastRecordedLevel = level;
        recordListener.onRecord(new Measurement(SENSOR, Long.valueOf(level)));
    }

}
