package com.sportpulse.ms_fixtures.model;

import java.time.OffsetDateTime;

public record Fixture(
    Long id,
    OffsetDateTime date,
    FixtureStatus statusShort,
    String statusLong,
    LeagueInfo league,
    TeamSnapshot homeTeam,
    TeamSnapshot awayTeam,
    VenueInfo venue,
    GoalsInfo goals
) {
    public record LeagueInfo(Long id, String name, String round) {}
    public record VenueInfo(String name, String city) {}
    public record GoalsInfo(Integer home, Integer away) {}
}