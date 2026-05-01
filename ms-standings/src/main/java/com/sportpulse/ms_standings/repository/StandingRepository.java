package com.sportpulse.ms_standings.repository;

import com.sportpulse.ms_standings.models.dto.response.ApiStandingResponse;

public interface StandingRepository {
    ApiStandingResponse getStandings(Integer league, Integer season );
}
