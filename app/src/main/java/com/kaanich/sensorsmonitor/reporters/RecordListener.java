package com.kaanich.sensorsmonitor.reporters;

import com.kaanich.sensorsmonitor.models.Measurement;

public interface RecordListener {
    void onRecord(Measurement measurement);
}
