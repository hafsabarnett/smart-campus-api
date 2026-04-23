package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// this class holds all the data for the application
// using static maps so the data stays around between requests
public class DataStore {

    // storing rooms, sensors and readings in separate maps
    // using the id as the key to look things up quickly
    // ConcurrentHashMap is used because it is thread safe
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // this block runs once when the app starts
    // adding sample data so the api isnt empty
    static {

        // create some rooms
        Room r1 = new Room("LIB-301", "Library Quiet Study", 40);
        Room r2 = new Room("LAB-101", "Computer Science Lab", 30);

        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        // create some sensors and assign them to rooms
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 21.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 412.0, "LIB-301");
        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "LAB-101");

        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);

        // link sensors to their rooms
        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
        r2.getSensorIds().add(s3.getId());

        // give each sensor an empty list to store readings in
        readings.put(s1.getId(), new ArrayList());
        readings.put(s2.getId(), new ArrayList());
        readings.put(s3.getId(), new ArrayList());
    }
}