package com.sportpulse.ms_fixtures.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.time.Instant;
import com.sportpulse.ms_fixtures.dto.ErrorDto;
import com.sportpulse.ms_fixtures.constants.ErrorCodes;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlanRestrictionException.class)
    public ResponseEntity<ErrorDto> handlePlanRestriction(PlanRestrictionException ex) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorDto(
                ErrorCodes.PLAN_RESTRICTION,
                ex.getMessage(),
                Instant.now().toString()
            ));
    }
}