package com.sportpulse.ms_fixtures.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    @ExceptionHandler(FixtureNotFoundException.class)
    public ResponseEntity<ErrorDto> handleFixtureNotFound(FixtureNotFoundException ex) {

    	ErrorDto error = new ErrorDto(
    			"FIXTURE_NOT_FOUND",
    			"Match not exist",
    			Instant.now().toString()
    			);
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
   }

}