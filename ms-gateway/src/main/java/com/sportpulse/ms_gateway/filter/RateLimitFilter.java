package com.sportpulse.ms_gateway.filter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter {

    private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();
    private static final int LIMIT = 60;
    private static final long WINDOW_SECONDS = 60;
    private static final long WINDOW_MS = WINDOW_SECONDS * 1000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = getClientIp(exchange);
        long now = System.currentTimeMillis();

        synchronized (requestLog.computeIfAbsent(ip, k -> new ArrayList<>())) {
            List<Long> requests = requestLog.get(ip);
            requests.removeIf(ts -> now - ts > WINDOW_MS);

            if (requests.size() >= LIMIT) {
                long oldestTimestamp = requests.get(0);
                long retryAfter = (oldestTimestamp + WINDOW_MS - now) / 1000;

                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("retry-after", String.valueOf(Math.max(1, retryAfter)));
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                String body = String.format(
                    "{\"error\":\"Too many requests\",\"retryAfter\":%d}",
                    Math.max(1, retryAfter)
                );
                return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes()))
                );
            }

            requests.add(now);
        }

        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        var forwarder = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        return forwarder != null ? forwarder.split(",")[0].trim()
            : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}


