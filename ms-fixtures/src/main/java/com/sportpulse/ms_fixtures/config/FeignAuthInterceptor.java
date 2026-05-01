package com.sportpulse.ms_fixtures.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthInterceptor {

    private static final String HEADER_X_USER_ID = "X-User-Id";

    @Bean
    public RequestInterceptor authInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                String userId = request.getHeader(HEADER_X_USER_ID);
                if (authHeader != null) {
                    template.header("Authorization", authHeader);
                }
                if (userId != null) {
                    template.header(HEADER_X_USER_ID, userId);
                }
            }
        };
    }
}