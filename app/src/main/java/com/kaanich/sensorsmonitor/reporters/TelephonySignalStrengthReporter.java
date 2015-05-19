package com.kaanich.sensorsmonitor.reporters;

import android.content.Context;
import android.telephony.*;

import com.kaanich.sensorsmonitor.models.Measurement;
import com.kaanich.sensorsmonitor.models.Metric;
import com.kaanich.sensorsmonitor.models.Sensor;

import lombok.Getter;

public class TelephonySignalStrengthReporter extends PhoneStateListener implements Reporter {

    private RecordListener recordListener;
    private TelephonyManager telephonyManager;
    public static Sensor SENSOR = new Sensor(GSM_SIGNAL_STRENGTH, Metric.PERCENTAGE);

    @Getter public String name = GSM_SIGNAL_STRENGTH;

    public TelephonySignalStrengthReporter(Context context) {
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void onCreate(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    @Override
    public void onStart() {
        telephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onStop() {
        telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        int strength = signalStrength.getGsmSignalStrength();
        if (strength == 99) {
            return;
        }
        strength = (strength * 100) / 31;
        recordListener.onRecord(new Measurement(SENSOR, Long.valueOf(strength)));
    }

}
