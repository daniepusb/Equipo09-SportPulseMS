package com.sportpulse.ms_auth.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Standard response of the API")
public record ErrorResponse(
        @Schema(description = "Name of the HTTP error", example = "Bad Request")
        String error,

        @Schema(description = "Descriptive message of the error", example = "Password must be at least 8 characters")
        String message,

        @Schema(description = "Timestamp of the error", example = "2026-04-23T16:30:00")
        LocalDateTime timestamp,
) {
}