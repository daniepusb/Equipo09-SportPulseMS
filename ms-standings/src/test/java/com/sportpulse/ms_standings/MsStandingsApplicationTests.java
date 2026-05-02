package com.sportpulse.ms_standings;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.sportpulse.ms_standings.config.TeamsClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class MsStandingsApplicationTests {

	@MockBean
	private TeamsClient teamsClient;

	@Test
	void contextLoads() {
	}

}