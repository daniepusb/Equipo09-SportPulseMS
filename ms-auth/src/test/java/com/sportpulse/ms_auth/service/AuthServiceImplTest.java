package com.sportpulse.ms_auth.service;

import com.sportpulse.ms_auth.common.exception.DuplicateEmailException;
import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.RegisterResponse;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import com.sportpulse.ms_auth.common.model.entities.UserEntity;
import com.sportpulse.ms_auth.common.model.mapper.UserMapper;
import com.sportpulse.ms_auth.repository.UserEntityRepository;
import com.sportpulse.ms_auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userEntityRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                userMapper
        );
    }

    @Test
    void createUser_success() {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "Password1");
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        UserEntity savedUser = UserEntity.builder()
                .id(userId)
                .username("john")
                .email("john@test.com")
                .password("encoded")
                .role(com.sportpulse.ms_auth.common.enums.UserRole.USER)
                .createdAt(createdAt)
                .build();

        when(userEntityRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(userMapper.toUserEntity(any(RegisterRequest.class))).thenReturn(savedUser);
        when(passwordEncoder.encode("Password1")).thenReturn("encoded");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        RegisterResponse result = authService.createUser(request);

        assertNotNull(result);
        assertEquals(userId.toString(), result.id());
        assertEquals("john", result.username());
        assertEquals("john@test.com", result.email());
        assertEquals("USER", result.role());
        assertEquals(createdAt, result.createdAt());
        verify(userEntityRepository).findByEmail("john@test.com");
        verify(userEntityRepository).save(any(UserEntity.class));
        verify(jwtService, never()).generateToken(any(), any(), any(), any());
    }

    @Test
    void createUser_duplicateEmail_throwsDuplicateEmailException() {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "Password1");

        when(userEntityRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(UserEntity.builder().email("john@test.com").build()));

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> authService.createUser(request)
        );

        assertEquals("El email ya está registrado.", exception.getMessage());
        verify(userEntityRepository, never()).save(any());
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("john@test.com", "Password1");
        UUID userId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .id(userId)
                .username("john")
                .email("john@test.com")
                .role(com.sportpulse.ms_auth.common.enums.UserRole.USER)
                .build();
        Authentication authentication = mock(Authentication.class);
        TokenResponse tokenResponse = new TokenResponse("jwt-token", "Bearer", 3600L, userId.toString());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken("john@test.com", userId.toString(), "john", "USER")).thenReturn(tokenResponse);

        TokenResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_invalidCredentials_throwsException() {
        LoginRequest request = new LoginRequest("john@test.com", "WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Bad credentials", exception.getMessage());
    }
}