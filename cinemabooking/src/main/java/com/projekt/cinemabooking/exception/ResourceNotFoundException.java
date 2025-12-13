package com.projekt.cinemabooking.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id) {
        super("Zasób o id: " + id + " nie został znaleziony.");
    }
}
