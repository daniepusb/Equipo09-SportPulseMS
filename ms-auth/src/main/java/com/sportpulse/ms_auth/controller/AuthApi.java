package com.sportpulse.ms_auth.controller;

import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.TokenPayload;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthApi {

    @PostMapping("/register")
    ResponseEntity<TokenResponse> createUser(@RequestBody @Valid RegisterRequest registerRequest);

    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest);

    @PostMapping("/validate")
    ResponseEntity<TokenPayload> validateToken(@RequestHeader("Authorization") String authHeader);
}
