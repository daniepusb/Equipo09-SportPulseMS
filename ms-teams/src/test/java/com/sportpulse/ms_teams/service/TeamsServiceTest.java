package com.sportpulse.ms_teams.service;

import com.sportpulse.ms_teams.model.Stadium;
import com.sportpulse.ms_teams.model.Team;
import com.sportpulse.ms_teams.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void whenTeamExists_thenReturnsTeam() {
        var team = new Team(529L, "FC Barcelona", "Spain", "logo.png", 1899, false, null);
        when(teamRepository.findById(529L)).thenReturn(team);

        var result = teamService.getTeamById(529L);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("FC Barcelona");
    }

    @Test
    void whenTeamNotExists_thenReturnsNull() {
        when(teamRepository.findById(999L)).thenReturn(null);

        var result = teamService.getTeamById(999L);

        assertThat(result).isNull();
    }
}