package com.sportpulse.ms_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private static final String TIMESTAMP = "timestamp";

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void handleResponseStatusExceptionReturnsUnifiedErrorFormat() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());

        handler.handle(exchange, new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payload")).block();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        String body = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(body, Map.class);

        assertEquals("BAD_REQUEST", response.get("error"));
        assertEquals("Invalid payload", response.get("message"));
        assertTrue(response.containsKey(TIMESTAMP));
        assertFalse(response.containsKey("status"));
        assertFalse(response.containsKey("path"));

        assertTrue(String.valueOf(response.get(TIMESTAMP)).endsWith("Z"));
        assertDoesNotThrow(() -> Instant.parse(String.valueOf(response.get(TIMESTAMP))));
    }

    @Test
    void handleGenericExceptionReturns503WithUnifiedErrorFormat() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());

        handler.handle(exchange, new RuntimeException("boom")).block();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        String body = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(body, Map.class);

        assertEquals("SERVICE_UNAVAILABLE", response.get("error"));
        assertEquals("Service is currently unavailable. Please try again later.", response.get("message"));
        assertTrue(response.containsKey(TIMESTAMP));
        assertFalse(response.containsKey("status"));
        assertFalse(response.containsKey("path"));

        assertTrue(String.valueOf(response.get(TIMESTAMP)).endsWith("Z"));
        assertDoesNotThrow(() -> Instant.parse(String.valueOf(response.get(TIMESTAMP))));
    }
}