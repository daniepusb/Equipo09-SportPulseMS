package com.sportpulse.ms_auth.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

@Builder
@Schema(description = "Standard response of the API")
public record ErrorResponse(
        @Schema(description = "Machine-readable error code", example = "USER_ALREADY_EXISTS")
        String error,

        @Schema(description = "Descriptive message of the error", example = "Ya existe un usuario con ese email")
        String message,

        @Schema(description = "Timestamp of the error", example = "2025-01-15T10:30:00Z")
        Instant timestamp){
}