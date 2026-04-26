package com.sportpulse.ms_auth.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Response for user registration")
public record RegisterResponse(
        @Schema(description = "ID of the created user", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Username", example = "javier_ruiz")
        String username,

        @Schema(description = "Email", example = "javier@email.com")
        String email,

        @Schema(description = "User role", example = "USER")
        String role,

        @Schema(description = "Creation timestamp", example = "2025-01-15T10:30:00Z")
        Instant createdAt
) {
}
