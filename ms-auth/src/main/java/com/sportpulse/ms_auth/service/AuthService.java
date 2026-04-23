package com.sportpulse.ms_auth.service;

import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.TokenPayload;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;

public interface AuthService {
    TokenResponse createUser (RegisterRequest registerRequest);
    TokenResponse login (LoginRequest loginRequest);
    TokenPayload validateToken(String token);
}
