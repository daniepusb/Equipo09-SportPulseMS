package com.sportpulse.ms_leagues.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error",     "MISSING_PARAMETER",
            "message",   "The parameter '" + ex.getParameterName() + "' is required",
            "timestamp", TIMESTAMP_FORMATTER.format(Instant.now())
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        if ("LEAGUE_NOT_FOUND".equals(ex.getReason())) {
            message = "No existe una liga con el ID proporcionado";
        }
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
            "error",     ex.getReason() != null ? ex.getReason() : "ERROR",
            "message",   message,
            "timestamp", TIMESTAMP_FORMATTER.format(Instant.now())
        ));
    }
}