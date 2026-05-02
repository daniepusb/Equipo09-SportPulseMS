package com.sportpulse.ms_teams.exception;

import com.sportpulse.ms_teams.constants.ErrorCodes;
import com.sportpulse.ms_teams.constants.ErrorMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Map;
import com.sportpulse.ms_teams.dto.ErrorDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(
            new ErrorDto(
                ErrorCodes.MISSING_PARAMETER,
                ErrorMessages.REQUIRED_PARAM_PREFIX + ex.getParameterName() + ErrorMessages.REQUIRED_PARAM_SUFFIX,
                Instant.now().toString()
            )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(
            new ErrorDto(
                ErrorCodes.TEAM_NOT_FOUND,
                ex.getReason() != null ? ex.getReason() : ErrorCodes.ERROR,
                Instant.now().toString()
            )
        );
    }
}