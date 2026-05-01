package com.sportpulse.ms_standings.service;

import com.sportpulse.ms_standings.models.dto.response.StandingResponse;

public interface StandingService {
    StandingResponse getStandings(Integer league, Integer season);
}
