package com.sportpulse.ms_teams.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error",     "MISSING_PARAMETER",
            "message",   "The parameter '" + ex.getParameterName() + "' is required",
            "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
            "error",     ex.getReason() != null ? ex.getReason() : "ERROR",
            "message",   ex.getMessage(),
            "timestamp", Instant.now().toString()
        ));
    }
}