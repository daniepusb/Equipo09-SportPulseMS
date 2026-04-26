package com.sportpulse.ms_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "SERVER_PORT=8080",
    "AUTH_SERVICE_URL=http://localhost:8081",
    "LEAGUES_SERVICE_URL=http://localhost:8082",
    "TEAMS_SERVICE_URL=http://localhost:8083",
    "FIXTURES_SERVICE_URL=http://localhost:8084",
    "STANDINGS_SERVICE_URL=http://localhost:8085",
    "NOTIFICATIONS_SERVICE_URL=http://localhost:8086",
    "DASHBOARD_SERVICE_URL=http://localhost:8087",
    "REDIS_HOST=localhost",
    "REDIS_PORT=6379"
})
class MsGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}

