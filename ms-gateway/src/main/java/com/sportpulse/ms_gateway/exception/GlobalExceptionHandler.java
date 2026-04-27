package com.sportpulse.ms_gateway.exception;

import java.time.Instant;

import com.sportpulse.ms_gateway.constants.GatewayErrorCodes;
import com.sportpulse.ms_gateway.constants.GatewayMessages;
import com.sportpulse.ms_gateway.dto.ErrorResponse;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import reactor.core.publisher.Mono;

@Component
@Order(-2) // Prioridad sobre el manejador por defecto de Spring
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String error;
        String message;

        if (ex instanceof ResponseStatusException) {
            status = (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
            error = status.name();
            String reason = ((ResponseStatusException) ex).getReason();
            message = reason != null ? reason : status.getReasonPhrase();
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            error = GatewayErrorCodes.SERVICE_UNAVAILABLE;
            message = GatewayMessages.SERVICE_UNAVAILABLE;
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorDetails = ErrorResponse.builder()
            .error(error)
            .message(message)
            .timestamp(Instant.now())
            .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorDetails);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
