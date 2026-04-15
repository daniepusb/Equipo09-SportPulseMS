package com.sportpulse.ms_gateway.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter{

	private final Map<String, List<Long>> requestLog = new ConcurrentHashMap<>();
	private static final int LIMIT = 60;
	private static final long WINDOW = 60_0000;


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

	       String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
	        long now = System.currentTimeMillis();

	        requestLog.putIfAbsent(ip, new ArrayList<>());
	        List<Long> requests = requestLog.get(ip);

	        requests.removeIf(timestamp -> now - timestamp > WINDOW);

	        if (requests.size() >= LIMIT) {
	            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
	            return exchange.getResponse().setComplete();
	        }

	        requests.add(now);
	        return chain.filter(exchange);
	    }
	}


