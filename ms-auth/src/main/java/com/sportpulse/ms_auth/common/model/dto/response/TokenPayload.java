package com.sportpulse.ms_auth.common.model.dto.response;

public record TokenPayload(
        boolean valid,
        String userId,
        String username,
        String role
) {
}