package com.sportpulse.ms_auth.service.impl;

import com.sportpulse.ms_auth.common.exception.DuplicateEmailException;
import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.TokenPayload;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import com.sportpulse.ms_auth.common.model.entities.UserEntity;
import com.sportpulse.ms_auth.common.model.mapper.UserMapper;
import com.sportpulse.ms_auth.repository.UserEntityRepository;
import com.sportpulse.ms_auth.service.AuthService;
import com.sportpulse.ms_auth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public TokenResponse createUser(@Valid RegisterRequest registerRequest) {
        log.info("Intentando crear usuario para email: {}", registerRequest.email());

        if (userEntityRepository.findByEmail(registerRequest.email()).isPresent()) {
            log.warn("Intento de crear usuario con email existente: {}", registerRequest.email());
            throw new DuplicateEmailException("USER_ALREADY_EXISTS");
        }

        UserEntity userToSave = userMapper.toUserEntity(registerRequest);
        userToSave.setPassword(passwordEncoder.encode(registerRequest.password()));
        userToSave.setCreatedAt(Instant.now());

        UserEntity userCreated = userEntityRepository.save(userToSave);
        log.info("Usuario creado exitosamente con ID: {}", userCreated.getId());

        return jwtService.generateToken(
                userCreated.getEmail(),
                userCreated.getId().toString(),
                userCreated.getUsername(),
                userCreated.getRole().name());
    }

    @Override
    public TokenResponse login(@Valid LoginRequest loginRequest) {
        log.info("Intentando login para usuario: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();

        log.info("Login exitoso para usuario: {}", user.getEmail());
        return jwtService.generateToken(
                user.getEmail(),
                user.getId().toString(),
                user.getUsername(),
                user.getRole().name());
    }

    @Override
    public TokenPayload validateToken(String token) {
        return jwtService.validateToken(token);
    }
}