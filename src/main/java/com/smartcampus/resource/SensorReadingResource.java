package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

// handles reading history for a specific sensor
// this class is not registered directly, it is returned by
// the sub resource locator in SensorResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    // sensorId is passed in from SensorResource
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // get all readings for this sensor
    @GET
    public Response getReadings() {

        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found\"}")
                    .build();
        }

        // get the readings list, return empty list if none exist
        List<SensorReading> history = DataStore.readings.get(sensorId);
        if (history == null) {
            history = new ArrayList();
        }

        return Response.ok(history).build();
    }

    // add a new reading for this sensor
    // sensor must be ACTIVE to accept readings
    @POST
    public Response addReading(SensorReading reading) {

        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found\"}")
                    .build();
        }

        // cant add readings if sensor is in maintenance
        if (sensor.getStatus().equalsIgnoreCase("MAINTENANCE")) {
            throw new SensorUnavailableException("Sensor " + sensorId 
                    + " is in MAINTENANCE and cannot accept readings");
        }

        // create the reading with auto generated id and timestamp
        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.readings.get(sensorId).add(newReading);

        // update the sensors current value to match the latest reading
        sensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}