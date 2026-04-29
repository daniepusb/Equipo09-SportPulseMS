package com.sportpulse.ms_fixtures.config;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignAuthInterceptor {

 @Bean
 public RequestInterceptor authInterceptor() {
     return template -> {
         ServletRequestAttributes attributes =
             (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
         if (attributes != null) {
             HttpServletRequest request = attributes.getRequest();
             String authHeader = request.getHeader("Authorization");
             if (authHeader != null) {
                 template.header("Authorization", authHeader);
             }
         }
     };
 }
}