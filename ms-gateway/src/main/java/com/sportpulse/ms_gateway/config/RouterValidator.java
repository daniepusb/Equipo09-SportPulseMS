package com.sportpulse.ms_gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class RouterValidator {

        private final Predicate<ServerHttpRequest> securedRoutePredicate =
            request -> ApiPathConstants.OPEN_API_ENDPOINTS
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

        public boolean isSecured(ServerHttpRequest request) {
                return securedRoutePredicate.test(request);
        }

}
