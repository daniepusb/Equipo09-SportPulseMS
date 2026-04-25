package com.sportpulse.ms_auth.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload with user information extracted from the JWT token")
public record TokenPayload(
        @Schema(description = "Indica si el token es válido", example = "true")
        boolean valid,

        @Schema(description = "Unique ID of the user (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String userId,

        @Schema(description = "Username", example = "javier_ruiz")
        String username,

        @Schema(description = "Role of the user", example = "USER")
        String role,

        @Schema(description = "Error code if the token is not valid", example = "TOKEN_EXPIRED", nullable = true)
        String error
) {
}