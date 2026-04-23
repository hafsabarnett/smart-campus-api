package com.smartcampus.model;

import java.util.UUID;

// stores a single reading from a sensor
public class SensorReading {

    private String id; // using UUID so each reading has a unique id
    private long timestamp; // time the reading was taken in milliseconds
    private double value;

    // constructor
    public SensorReading() {
    }

    //second constructor (method overloading) 
    //creates a new reading with auto generated id and timestamp
    // pass in the value as parameter for initialisation
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}