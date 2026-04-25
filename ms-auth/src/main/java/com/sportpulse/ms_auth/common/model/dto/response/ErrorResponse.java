package com.sportpulse.ms_auth.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Respuesta de error estándar de la API")
public record ErrorResponse(
        @Schema(description = "Nombre del error HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensaje descriptivo del error", example = "Password must be at least 8 characters")
        String message,

        @Schema(description = "Timestamp del error", example = "2026-04-23T16:30:00")
        LocalDateTime timestamp,
) {
}