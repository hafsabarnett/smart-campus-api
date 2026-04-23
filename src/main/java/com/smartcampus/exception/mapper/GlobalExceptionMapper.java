package com.smartcampus.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// catches any exception that isnt handled by the other mappers
// makes sure we never return a raw stack trace to the client
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {

        // log the error on the server side so we can debug it
        LOG.log(Level.SEVERE, "Unexpected error occurred", ex);

        // return a generic message to the client
        Map<String, Object> error = new HashMap();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "Something went wrong on the server");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}