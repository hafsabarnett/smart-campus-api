package com.smartcampus.exception;

// thrown when a sensor is created with a roomId that doesnt exist
public class LinkedResourceNotFoundException extends RuntimeException {

    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}