package com.sportpulse.ms_auth.exception;

import com.sportpulse.ms_auth.common.constants.ErrorCodes;
import com.sportpulse.ms_auth.common.exception.AccessDeniedException;
import com.sportpulse.ms_auth.common.exception.DuplicateEmailException;
import com.sportpulse.ms_auth.common.exception.GlobalExceptionHandler;
import com.sportpulse.ms_auth.common.exception.InvalidCredentialsException;
import com.sportpulse.ms_auth.common.model.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleInvalidCredentialsReturns401() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(
                new InvalidCredentialsException("Invalid credentials"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ErrorCodes.INVALID_CREDENTIALS, response.getBody().error());
        assertEquals("Invalid credentials", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
        assertTrue(response.getBody().timestamp().toString().endsWith("Z"));
    }

    @Test
    void handleBadCredentialsReturns401() {
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(
                new BadCredentialsException("Bad credentials"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ErrorCodes.INVALID_CREDENTIALS, response.getBody().error());
        assertEquals("Invalid credentials", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleAccessDeniedReturns403() {
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("Access denied"), request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ErrorCodes.ACCESS_DENIED, response.getBody().error());
        assertEquals("Access denied", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleDuplicateEmailReturns409() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmail(
                new DuplicateEmailException("A user with that email already exists"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ErrorCodes.USER_ALREADY_EXISTS, response.getBody().error());
        assertEquals("A user with that email already exists", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleValidationErrorsReturns400() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new Object(), "registerRequest");
        bindingResult.addError(new FieldError("registerRequest", "email", "must be a valid email"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCodes.VALIDATION_ERROR, response.getBody().error());
        assertTrue(response.getBody().message().contains("email"));
        assertNotNull(response.getBody().timestamp());
    }
}