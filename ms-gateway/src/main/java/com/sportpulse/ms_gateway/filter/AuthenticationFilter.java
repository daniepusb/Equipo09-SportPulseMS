package com.sportpulse.ms_gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sportpulse.ms_gateway.config.ApiPathConstants;
import com.sportpulse.ms_gateway.config.RouterValidator;
import com.sportpulse.ms_gateway.constants.GatewayHeaders;
import com.sportpulse.ms_gateway.constants.GatewayMessages;
import com.sportpulse.ms_gateway.dto.ErrorResponse;
import com.sportpulse.ms_gateway.dto.TokenPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.Instant;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;
    private final RouterValidator routerValidator;
    private final ObjectMapper objectMapper = new ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Value("${AUTH_SERVICE_URL}")
    private String authServiceUrl;

    public AuthenticationFilter(WebClient.Builder webClientBuilder, RouterValidator routerValidator) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.routerValidator = routerValidator;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Verificar si la ruta requiere autenticación
            if (routerValidator.isSecured(request)) {
                
                // 2. Verificar si existe el header Authorization
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, GatewayMessages.AUTH_HEADER_MISSING, HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith(GatewayHeaders.BEARER_PREFIX)) {
                    return onError(exchange, GatewayMessages.TOKEN_FORMAT_INVALID, HttpStatus.UNAUTHORIZED);
                }

                // 3. Validar token contra el microservicio ms-auth
                return webClientBuilder.build()
                        .post()
                    .uri(authServiceUrl + ApiPathConstants.AUTH_VALIDATE)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .retrieve()
                        .bodyToMono(TokenPayload.class)
                        .flatMap(payload -> {
                            if (payload != null && payload.isValid()) {
                                 // 4. Enriquecer la request con info del usuario para los MS destino
                                 ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                     .header(HttpHeaders.AUTHORIZATION, authHeader)
                                     .header(GatewayHeaders.X_USER_ID, payload.getUserId())
                                     .header(GatewayHeaders.X_USER_ROLE, payload.getRole())
                                     .header(GatewayHeaders.X_USER_NAME, payload.getUsername())
                                         .build();

                                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                            } else {
                                return onError(exchange, GatewayMessages.INVALID_OR_EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED);
                            }
                        })
                        .onErrorResume(e -> onError(exchange, GatewayMessages.AUTHENTICATION_ERROR, HttpStatus.UNAUTHORIZED));
            }
            
            // Si no es segura (es pública), continuar sin validar
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(httpStatus.name())
                .message(message)
                .timestamp(Instant.now())
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}
