package com.sportpulse.ms_teams.exception;

import com.sportpulse.ms_teams.controller.TeamsController;
import com.sportpulse.ms_teams.dto.TeamResponse;
import com.sportpulse.ms_teams.mapper.TeamMapper;
import com.sportpulse.ms_teams.model.Team;
import com.sportpulse.ms_teams.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamsController.class)
class TeamsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @MockBean
    private TeamMapper teamMapper;

    @Test
    void whenLeagueMissing_thenReturns400() throws Exception {
        mockMvc.perform(get("/api/teams")
                .param("season", "2024")
                .header("X-User-Id", "test-user"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("MISSING_PARAMETER"));
    }

    @Test
    void whenUserIdMissing_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/teams")
                .param("league", "140")
                .param("season", "2024"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void whenTeamNotFound_thenReturns404() throws Exception {
        when(teamService.getTeamById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/teams/999")
                .header("X-User-Id", "test-user"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("TEAM_NOT_FOUND"));
    }

    @Test
    void whenValidRequest_thenReturns200() throws Exception {
        var team = new Team(529L, "FC Barcelona", "Spain", "logo.png", 1899, false, null);
        var dto  = new TeamResponse(529L, "FC Barcelona", "Spain", "logo.png", 1899, false, null);

        when(teamService.getTeamsByLeagueAndSeason(140, 2024)).thenReturn(List.of(team));
        when(teamMapper.toResponse(team)).thenReturn(dto);

        mockMvc.perform(get("/api/teams")
                .param("league", "140")
                .param("season", "2024")
                .header("X-User-Id", "test-user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("FC Barcelona"));
    }
}