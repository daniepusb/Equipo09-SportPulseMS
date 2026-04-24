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
@Tag(name = "Auth", description = "Operaciones de autenticación")
public interface AuthApi {

    @Operation(
            summary = "Registrar usuario",
            description = "Crea un nuevo usuario en el sistema y retorna un token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o password no cumple requisitos",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping("/register")
    ResponseEntity<TokenResponse> createUser(@Valid @RequestBody RegisterRequest registerRequest);

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario existente y retorna un token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest);

    @Operation(
            summary = "Validar token",
            description = "Valida un token JWT y retorna la información del usuario. " +
                    "Este endpoint es consumido por otros microservicios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token procesado (válido o inválido)",
                    content = @Content(schema = @Schema(implementation = TokenPayload.class))),
            @ApiResponse(responseCode = "400", description = "Header Authorization requerido",
                    content = @Content)
    })
    @PostMapping("/validate")
    ResponseEntity<TokenPayload> validateToken(@RequestHeader("Authorization") String authHeader);
}