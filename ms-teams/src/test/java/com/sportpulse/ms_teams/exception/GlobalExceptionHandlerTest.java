package com.sportpulse.ms_teams.exception;

import com.sportpulse.ms_teams.controller.TeamsController;
import com.sportpulse.ms_teams.constants.ErrorCodes;
import com.sportpulse.ms_teams.constants.HeaderConstants;
import com.sportpulse.ms_teams.constants.ResponseFields;
import com.sportpulse.ms_teams.mapper.TeamMapper;
import com.sportpulse.ms_teams.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamsController.class)
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

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
                .header(HeaderConstants.X_USER_ID, "test-user"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(ErrorCodes.MISSING_PARAMETER))
            .andExpect(jsonPath("$." + ResponseFields.MESSAGE).exists())
            .andExpect(jsonPath("$." + ResponseFields.TIMESTAMP).exists());
    }

    @Test
    void whenTeamNotFound_thenReturns404() throws Exception {
        when(teamService.getTeamById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/teams/999")
                .header(HeaderConstants.X_USER_ID, "test-user"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(ErrorCodes.TEAM_NOT_FOUND));
    }
}