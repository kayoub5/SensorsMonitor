package com.kaanich.sensorsmonitor.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.*;
import com.j256.ormlite.table.DatabaseTable;

import lombok.Data;

@DatabaseTable(tableName = "sensor")
@Data
public class Sensor {
    @DatabaseField(id = true)
    private String name;

    @DatabaseField
    private Long refreshInterval;

    @DatabaseField(foreign = true, canBeNull = false)
    private Metric metric;

    @ForeignCollectionField
    private ForeignCollection<Measurement> measurements;

    public Sensor(String name, Metric metric) {
        this();
        this.name = name;
        this.metric = metric;
    }

    public Sensor() {
        super();
    }

}
