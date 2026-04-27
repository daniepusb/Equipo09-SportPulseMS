package com.sportpulse.ms_teams.exception;

import com.sportpulse.ms_teams.constants.ErrorCodes;
import com.sportpulse.ms_teams.constants.ErrorMessages;
import com.sportpulse.ms_teams.constants.ResponseFields;
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
            ResponseFields.ERROR, ErrorCodes.MISSING_PARAMETER,
            ResponseFields.MESSAGE, ErrorMessages.REQUIRED_PARAM_PREFIX + ex.getParameterName() + ErrorMessages.REQUIRED_PARAM_SUFFIX,
            ResponseFields.TIMESTAMP, Instant.now().toString()
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
            ResponseFields.ERROR, ex.getReason() != null ? ex.getReason() : ErrorCodes.ERROR,
            ResponseFields.MESSAGE, ex.getMessage(),
            ResponseFields.TIMESTAMP, Instant.now().toString()
        ));
    }
}