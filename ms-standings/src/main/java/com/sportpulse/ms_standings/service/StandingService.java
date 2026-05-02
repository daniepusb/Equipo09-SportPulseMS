package com.sportpulse.ms_standings.service;

import com.sportpulse.ms_standings.models.response.StandingResponse;

public interface StandingService {
    StandingResponse getStandings(Integer league, Integer season);
    StandingResponse getTeamStanding(Long teamId, Integer league, Integer season);
}
