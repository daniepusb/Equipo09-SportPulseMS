package com.sportpulse.ms_gateway.filter;

import com.sportpulse.ms_gateway.config.RouterValidator;
import com.sportpulse.ms_gateway.dto.TokenPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient.Builder webClientBuilder;
    private final RouterValidator routerValidator;

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
            if (routerValidator.isSecured.test(request)) {
                
                // 2. Verificar si existe el header Authorization
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return onError(exchange, "Falta el encabezado de autorización", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return onError(exchange, "Formato de token inválido", HttpStatus.UNAUTHORIZED);
                }

                // 3. Validar token contra el microservicio ms-auth
                return webClientBuilder.build()
                        .post()
                        .uri(authServiceUrl + "/api/auth/validate")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .retrieve()
                        .bodyToMono(TokenPayload.class)
                        .flatMap(payload -> {
                            if (payload != null && payload.isValid()) {
                                // 4. Enriquecer la request con info del usuario para los MS destino
                                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                        .header("X-User-Id", payload.getUserId())
                                        .header("X-User-Role", payload.getRole())
                                        .header("X-User-Name", payload.getUsername())
                                        .build();

                                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                            } else {
                                return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
                            }
                        })
                        .onErrorResume(e -> onError(exchange, "Error de autenticación centralizada", HttpStatus.UNAUTHORIZED));
            }
            
            // Si no es segura (es pública), continuar sin validar
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
