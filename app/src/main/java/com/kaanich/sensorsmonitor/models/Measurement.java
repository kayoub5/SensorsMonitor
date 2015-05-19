package com.kaanich.sensorsmonitor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.Data;

@DatabaseTable(tableName = "measurement")
@Data
public class Measurement {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true)
    private Sensor sensor;

    @DatabaseField(canBeNull = false)
    private Date timestamp;

    @DatabaseField(canBeNull = false)
    private Long value;

    public Measurement() {
        this.timestamp = new Date();
    }

    public Measurement(Sensor sensor,Long value) {
        this();
        this.sensor = sensor;
        this.value = value;
    }

}
