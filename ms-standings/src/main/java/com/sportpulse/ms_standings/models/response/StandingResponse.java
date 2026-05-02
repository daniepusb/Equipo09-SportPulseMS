package com.sportpulse.ms_standings.models.response;

import java.io.Serializable;
import java.util.List;

public record StandingResponse(
        LeagueInfo league,
        List<TeamStanding> standings
) implements Serializable {
    public record LeagueInfo(Long id, String name, String country, Integer season) implements Serializable {}
    public record TeamStanding(Integer rank, TeamInfo team, Integer points,
                               Integer played, Integer won, Integer drawn,
                               Integer lost, Integer goalsFor, Integer goalsAgainst,
                               Integer goalDifference, String form, String description) implements Serializable {}
    public record TeamInfo(Long id, String name, String logo) implements Serializable {}
}