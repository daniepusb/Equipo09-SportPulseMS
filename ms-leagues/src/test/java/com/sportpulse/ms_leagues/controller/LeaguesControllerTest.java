package com.sportpulse.ms_leagues.controller;

import com.sportpulse.ms_leagues.dto.LeagueCurrentSeason;
import com.sportpulse.ms_leagues.dto.LeagueDetailResponse;
import com.sportpulse.ms_leagues.dto.LeagueResponse;
import com.sportpulse.ms_leagues.mapper.LeagueMapper;
import com.sportpulse.ms_leagues.model.League;
import com.sportpulse.ms_leagues.service.LeagueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaguesController.class)
@TestPropertySource(properties = "SERVER_PORT=0")
class LeaguesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeagueService leagueService;

    @MockBean
    private LeagueMapper leagueMapper;

    @Test
    void whenUserIdMissing_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/leagues")
                .param("country", "Spain")
                .param("season", "2024"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Missing authenticated user context"));
    }

    @Test
    void whenUserIdBlank_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/leagues")
                .param("country", "Spain")
                .param("season", "2024")
                .header("X-User-Id", "   "))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Missing authenticated user context"));
    }

    @Test
    void whenValidListRequest_thenReturns200() throws Exception {
        League league = new League(
            140L,
            "La Liga",
            "League",
            "logo.png",
            "Spain",
            List.of(2024),
            2024,
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 5, 30)
        );
        LeagueResponse response = new LeagueResponse(
            140L,
            "La Liga",
            "League",
            "logo.png",
            "Spain",
            2024,
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 5, 30)
        );

        when(leagueService.getAllLeagues("Spain", 2024)).thenReturn(List.of(league));
        when(leagueMapper.toResponse(league)).thenReturn(response);

        mockMvc.perform(get("/api/leagues")
                .param("country", "Spain")
                .param("season", "2024")
                .header("X-User-Id", "user-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("La Liga"))
            .andExpect(jsonPath("$[0].country").value("Spain"));

        verify(leagueService).getAllLeagues("Spain", 2024);
    }

    @Test
    void whenLeagueNotFound_thenReturns404() throws Exception {
        when(leagueService.getLeagueById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/leagues/999")
                .header("X-User-Id", "user-1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("LEAGUE_NOT_FOUND"));

        verify(leagueService).getLeagueById(999L);
    }

    @Test
    void whenValidLeagueId_thenReturns200() throws Exception {
        League league = new League(
            140L,
            "La Liga",
            "League",
            "logo.png",
            "Spain",
            List.of(2024, 2025),
            2024,
            LocalDate.of(2024, 8, 1),
            LocalDate.of(2025, 5, 30)
        );
        LeagueDetailResponse detailResponse = new LeagueDetailResponse(
            140L,
            "La Liga",
            "League",
            "logo.png",
            "Spain",
            List.of(2024, 2025),
            new LeagueCurrentSeason(
                2024,
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2025, 5, 30),
                true
            )
        );

        when(leagueService.getLeagueById(140L)).thenReturn(league);
        when(leagueMapper.toDetailResponse(league)).thenReturn(detailResponse);

        mockMvc.perform(get("/api/leagues/140")
                .header("X-User-Id", "user-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(140))
            .andExpect(jsonPath("$.name").value("La Liga"))
            .andExpect(jsonPath("$.currentSeason.year").value(2024));

        verify(leagueService).getLeagueById(140L);
    }
}
