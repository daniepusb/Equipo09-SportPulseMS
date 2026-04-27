package com.sportpulse.ms_auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "DB_HOST=localhost",
    "DB_PORT=5432",
    "DB_NAME=ms_auth",
    "DB_USERNAME=postgres",
    "DB_PASSWORD=postgres",
    "REDIS_HOST=localhost",
    "REDIS_PORT=6379",
    "SERVER_PORT=8081",
    "JWT_SECRET=test-secret-key-for-testing-purposes-only",
    "JWT_ACCESS_EXPIRATION=3600000",
    "JWT_REFRESH_EXPIRATION=86400000"
})
class MsAuthApplicationTests {

    @Test
    void contextLoads() {
    }

}