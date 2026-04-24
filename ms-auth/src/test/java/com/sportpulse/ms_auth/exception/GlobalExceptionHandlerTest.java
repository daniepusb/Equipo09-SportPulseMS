package com.sportpulse.ms_auth.exception;

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
import static org.mockito.Mockito.when;

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
    void handleInvalidCredentials_returns401() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(
                new InvalidCredentialsException("Credenciales incorrectas"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().status());
        assertEquals("Credenciales incorrectas", response.getBody().message());
        assertEquals("/api/auth/login", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleBadCredentials_returns401() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(
                new BadCredentialsException("Bad credentials"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody().message());
    }

    @Test
    void handleAccessDenied_returns403() {
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("Acceso denegado"), request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().status());
        assertEquals("Acceso denegado", response.getBody().message());
        assertEquals("Forbidden", response.getBody().error());
    }

    @Test
    void handleDuplicateEmail_returns409() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmail(
                new DuplicateEmailException("El email ya está registrado"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertEquals("El email ya está registrado", response.getBody().message());
        assertEquals("Conflict", response.getBody().error());
    }

    @Test
    void handleValidationErrors_returns400() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new Object(), "registerRequest");
        bindingResult.addError(new FieldError("registerRequest", "email", "must be a valid email"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertTrue(response.getBody().message().contains("email"));
    }
}