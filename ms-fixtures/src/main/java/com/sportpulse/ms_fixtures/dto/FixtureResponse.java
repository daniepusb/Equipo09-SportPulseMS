package com.sportpulse.ms_fixtures.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record FixtureResponse(
    Long id,
    OffsetDateTime date,
    StatusInfo status,
    LeagueInfo league,
    TeamInfo homeTeam,
    TeamInfo awayTeam,
    VenueInfo venue
) {
    public record StatusInfo(@JsonProperty("short") String shortCode, @JsonProperty("long") String longCode) {}
    public record LeagueInfo(Long id, String name, String round) {}
    public record TeamInfo(Long id, String name, String logo, Integer goals) {}
    public record VenueInfo(String name, String city) {}
}