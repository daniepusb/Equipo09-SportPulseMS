package com.sportpulse.ms_leagues.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import com.sportpulse.ms_leagues.model.League;
import com.sportpulse.ms_leagues.repository.LeagueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;

    @InjectMocks
    private LeagueService leagueService;

    @Test
    void getAllLeagues_shouldReturnListOfLeagues() {
        // Given
        var league = new League(
            1L,
            "Premier League",
            "League",
            "logo.png",
            "England",
            List.of(2023),
            2023,
            null,
            null
        );
        when(leagueRepository.findAll(null, null)).thenReturn(List.of(league));

        // When
        var result = leagueService.getAllLeagues(null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Premier League", result.get(0).name());
        verify(leagueRepository).findAll(null, null);
    }

    @Test
    void getLeagueById_shouldReturnLeague() {
        // Given
        var league = new League(
            140L,
            "La Liga",
            "League",
            "https://media.api-sports.io/football/leagues/140.png",
            "Spain",
            List.of(2025),
            2025,
            null,
            null
        );
        when(leagueRepository.findById(140L)).thenReturn(league);

        // When
        var result = leagueService.getLeagueById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Premier League", result.name());
        verify(leagueRepository).findById(1L);
    }
}