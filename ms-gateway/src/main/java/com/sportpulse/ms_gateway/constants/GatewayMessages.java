package com.sportpulse.ms_gateway.constants;

public final class GatewayMessages {

    private GatewayMessages() {
    }

    public static final String AUTH_HEADER_MISSING = "Authorization header is missing";
    public static final String TOKEN_FORMAT_INVALID = "Token format is invalid";
    public static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token";
    public static final String AUTHENTICATION_ERROR = "Centralized authentication error";
    public static final String SERVICE_UNAVAILABLE = "Service is currently unavailable. Please try again later.";
}