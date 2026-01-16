package com.projekt.cinemabooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Błąd walidacji danych", errors);
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // 409 Conflict (np. zajęte miejsce)
    @ExceptionHandler({SeatAlreadyTakenException.class, IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // 403 Forbidden
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Brak uprawnień", null);
    }

    // 500 - Zabezpieczone
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        ex.printStackTrace();
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Wystąpił błąd serwera. Skontaktuj się z administratorem.", null);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, Object details) {
        Map<String, Object> body = new LinkedHashMap<>(); // LinkedHashMap zachowuje kolejność
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (details != null) {
            body.put("details", details);
        }
        return new ResponseEntity<>(body, status);
    }
}