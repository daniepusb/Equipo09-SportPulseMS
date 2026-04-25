package com.sportpulse.ms_auth.controller;

import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.TokenPayload;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication and user management operations")
public interface AuthApi {

    @Operation(
            summary = "User Registration",
            description = "Creates a new user in the system and returns a JWT token for authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or password does not meet requirements",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "Email is already registered",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping("/register")
    ResponseEntity<TokenResponse> createUser(@Valid @RequestBody RegisterRequest registerRequest);

    @Operation(
            summary = "User Login",
            description = "Authenticates an existing user and returns a JWT token for authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or password does not meet requirements",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest);

    @Operation(
            summary = "Validate Token",
            description = "Validates a JWT token and returns the user information. " +
                    "This endpoint is consumed by other microservices."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Valid token - returns user information",
                    content = @Content(schema = @Schema(implementation = TokenPayload.class))),
            @ApiResponse(responseCode = "401", description = "Invalid token, expired or with incorrect signature",
                    content = @Content(schema = @Schema(implementation = TokenPayload.class))),
            @ApiResponse(responseCode = "400", description = "Authorization header required and must start with 'Bearer '",
                    content = @Content)
    })
    @PostMapping("/validate")
    ResponseEntity<TokenPayload> validateToken(@RequestHeader("Authorization") String authHeader);
}