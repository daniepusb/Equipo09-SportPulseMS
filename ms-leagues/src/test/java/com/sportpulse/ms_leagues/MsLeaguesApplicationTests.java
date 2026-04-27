package com.sportpulse.ms_leagues;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(properties = {
    "RAPIDAPI_KEY=test-key",
    "SERVER_PORT=8082"
})
class MsLeaguesApplicationTests {

    @Test
    void contextLoads() {
    }
}