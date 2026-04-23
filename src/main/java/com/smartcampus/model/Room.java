package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

// room class to store room info
public class Room {

    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds; // list of sensors in this room

    // default constructor
    public Room() {
        sensorIds = new ArrayList();
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.sensorIds = new ArrayList();
    }

    // getters and setters below

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // returns the list of sensor ids assigned to this room
    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
}