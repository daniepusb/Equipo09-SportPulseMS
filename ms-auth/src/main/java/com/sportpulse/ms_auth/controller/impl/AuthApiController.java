package com.sportpulse.ms_auth.controller.impl;

import com.sportpulse.ms_auth.common.constants.HeaderConstants;
import com.sportpulse.ms_auth.common.constants.JwtConstants;
import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.RegisterResponse;
import com.sportpulse.ms_auth.common.model.dto.response.TokenPayload;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import com.sportpulse.ms_auth.controller.AuthApi;
import com.sportpulse.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {
    private final AuthService authService;

    @Override
    public ResponseEntity<RegisterResponse> createUser(@RequestBody @Valid RegisterRequest registerRequest) {
        RegisterResponse response = authService.createUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        TokenResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TokenPayload> validateToken(@RequestHeader(HeaderConstants.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace(JwtConstants.BEARER_PREFIX, "");
        TokenPayload payload = authService.validateToken(token);

        if (payload.valid()) {
            return ResponseEntity.ok(payload);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payload);
    }
}
