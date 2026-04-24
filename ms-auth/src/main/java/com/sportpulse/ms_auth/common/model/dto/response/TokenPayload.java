package com.sportpulse.ms_auth.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload con información del usuario extraída del token JWT")
public record TokenPayload(
        @Schema(description = "Indica si el token es válido", example = "true")
        boolean valid,

        @Schema(description = "ID único del usuario (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String userId,

        @Schema(description = "Nombre de usuario", example = "javier_ruiz")
        String username,

        @Schema(description = "Rol del usuario", example = "USER")
        String role,

        @Schema(description = "Código de error si el token no es válido", example = "TOKEN_EXPIRED", nullable = true)
        String error
) {
}