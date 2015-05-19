package com.kaanich.sensorsmonitor.reporters;

public interface Reporter {

    String BATTERY_LEVEL = "reporters.battery_level";
    String GSM_SIGNAL_STRENGTH = "reporters.gsm_signal_strength";

    void onCreate(RecordListener recordListener);
    void onStart();
    void onStop();
    public String getName();
}
