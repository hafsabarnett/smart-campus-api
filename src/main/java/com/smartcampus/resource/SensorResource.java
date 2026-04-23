package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

// handles requests about sensors
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // get all sensors
    // can also filter by type using query parameter e.g ?type=CO2
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {

        List<Sensor> result = new ArrayList<>(DataStore.sensors.values());

        // filter by type if provided
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList();
            for (Sensor s : result) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filtered.add(s);
                }
            }
            return Response.ok(filtered).build();
        }

        return Response.ok(result).build();
    }

    // register a new sensor
    // the roomId must exist otherwise we return an error
    @POST
    public Response createSensor(Sensor sensor) {

        // basic validation
        if (sensor == null || sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Sensor id is required\"}")
                    .build();
        }

        // check sensor doesnt already exist
        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Sensor already exists\"}")
                    .build();
        }

        // make sure the room exists before adding the sensor
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "Room " + sensor.getRoomId() + " does not exist");
        }

        // default to ACTIVE if no status given
        if (sensor.getStatus() == null || sensor.getStatus().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // save sensor and link it to the room
        DataStore.sensors.put(sensor.getId(), sensor);
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        DataStore.readings.put(sensor.getId(), new ArrayList());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // get a single sensor by id
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {

        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found\"}")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    // update a sensors status or value
    @PUT
    @Path("/{sensorId}")
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updated) {

        Sensor existing = DataStore.sensors.get(sensorId);

        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found\"}")
                    .build();
        }

        // only update fields that were sent in the request
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        if (updated.getType() != null) {
            existing.setType(updated.getType());
        }
        existing.setCurrentValue(updated.getCurrentValue());

        return Response.ok(existing).build();
    }

    // sub resource locator for readings
    // passes the sensorId to SensorReadingResource to handle the request
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}