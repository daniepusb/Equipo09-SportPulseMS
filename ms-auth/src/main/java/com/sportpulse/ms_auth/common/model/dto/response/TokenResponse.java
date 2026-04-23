package com.sportpulse.ms_auth.common.model.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
String accessToken
) {
}
