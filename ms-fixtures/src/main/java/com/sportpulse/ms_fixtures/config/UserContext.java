package com.sportpulse.ms_fixtures.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserContext {

    private static final ThreadLocal<String> AUTH_HEADER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    public static void init() {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            AUTH_HEADER.set(request.getHeader("Authorization"));
            USER_ID.set(request.getHeader("X-User-Id"));
        }
    }

    public static void clear() {
        AUTH_HEADER.remove();
        USER_ID.remove();
    }

    public static UserContext get() {
        return new UserContext();
    }

    public String getAuthHeader() {
        return AUTH_HEADER.get();
    }

    public String getUserId() {
        return USER_ID.get();
    }
}