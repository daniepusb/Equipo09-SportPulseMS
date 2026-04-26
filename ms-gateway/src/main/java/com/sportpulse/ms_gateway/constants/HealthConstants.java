package com.sportpulse.ms_gateway.constants;

public final class HealthConstants {

    private HealthConstants() {
    }

    public static final String KEY_GATEWAY = "gateway";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_SERVICES = "services";

    public static final String STATUS_UP = "UP";
    public static final String STATUS_DOWN = "DOWN";

    public static final String ACTUATOR_HEALTH_PATH = "/actuator/health";
}