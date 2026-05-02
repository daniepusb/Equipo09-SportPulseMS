package com.sportpulse.ms_standings.repository;

import com.sportpulse.ms_standings.models.response.ApiStandingResponse;

public interface StandingRepository {
    ApiStandingResponse getStandings(Integer league, Integer season );
}
