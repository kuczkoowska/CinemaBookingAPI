package com.projekt.cinemabooking.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " o id: " + id + " nie został znaleziony.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
