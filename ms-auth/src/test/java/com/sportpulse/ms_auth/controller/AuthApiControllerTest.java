package com.sportpulse.ms_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportpulse.ms_auth.common.model.dto.request.LoginRequest;
import com.sportpulse.ms_auth.common.model.dto.request.RegisterRequest;
import com.sportpulse.ms_auth.common.model.dto.response.TokenResponse;
import com.sportpulse.ms_auth.repository.UserEntityRepository;
import com.sportpulse.ms_auth.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
class AuthApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        userEntityRepository.deleteAll();
    }

    @Test
    void register_withValidData_returns201() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "Password1");

        MvcResult result = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber())
            .andExpect(jsonPath("$.userId").exists())
                .andReturn();

        TokenResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), TokenResponse.class);
        assertNotNull(response.token());
    }

    @Test
    void register_withDuplicateEmail_returns409() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "duplicate@test.com", "Password1");
        authService.createUser(request);

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El email ya está registrado."));
    }

    @Test
    void register_withInvalidEmail_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "invalid-email", "Password1");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void register_withBlankUsername_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("", "john@test.com", "Password1");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_withPasswordWithoutUppercase_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "password1");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_withPasswordWithoutNumber_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "Password");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_withPasswordTooShort_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "Pass1");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_withBlankEmail_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "", "Password1");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_withBlankPassword_returns400() throws Exception {
        RegisterRequest request = new RegisterRequest("john", "john@test.com", "");

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void login_withValidCredentials_returns200() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("john", "john@test.com", "Password1");
        authService.createUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("john@test.com", "Password1");

        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").isNumber())
            .andExpect(jsonPath("$.userId").exists())
                .andReturn();

        TokenResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), TokenResponse.class);
        assertNotNull(response.token());
    }

    @Test
    void login_withInvalidPassword_returns401() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("john", "john@test.com", "Password1");
        authService.createUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("john@test.com", "WrongPassword");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void login_withNonExistentUser_returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@test.com", "Password1");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void login_withBlankEmail_returns400() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "Password1");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_withBlankPassword_returns400() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@test.com", "");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_withInvalidEmailFormat_returns400() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalid-email", "Password1");

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validate_withValidToken_returns200() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("john", "john@test.com", "Password1");
        TokenResponse tokenResponse = authService.createUser(registerRequest);
        String token = tokenResponse.token();

        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void validate_withInvalidToken_returns200WithValidFalse() throws Exception {
        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.userId").isEmpty())
                .andExpect(jsonPath("$.username").isEmpty())
                .andExpect(jsonPath("$.role").isEmpty());
    }

    @Test
    void validate_withMissingToken_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/validate"))
                .andExpect(status().isBadRequest());
    }
}