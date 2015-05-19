package com.kaanich.sensorsmonitor.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

@DatabaseTable(tableName = "metric")
@Data
public class Metric {

    static public Metric PERCENTAGE = new Metric("percentage", "%", Long.valueOf(0), Long.valueOf(100));

    @DatabaseField(id = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private String unit;

    @DatabaseField
    private Long min;

    @DatabaseField
    private Long max;

    public Metric() {
        super();
    }

    public Metric(String name, String unit) {
        this();
        this.name = name;
        this.unit = unit;
    }

    public Metric(String name, String unit, Long min, Long max) {
        this(name, unit);
        this.min = min;
        this.max = max;
    }

}
