package com.sportpulse.ms_standings.models.dto.response;

import java.util.List;

public record StandingResponse(
        LeagueInfo league,
        List<TeamStanding> standings
) {
    public record LeagueInfo(Long id, String name, String country, Integer season) {}
    public record TeamStanding(Integer rank, TeamInfo team, Integer points,
                               Integer played, Integer won, Integer drawn,
                               Integer lost, Integer goalsFor, Integer goalsAgainst,
                               Integer goalDifference, String form) {}
    public record TeamInfo(Long id, String name, String logo) {}
}