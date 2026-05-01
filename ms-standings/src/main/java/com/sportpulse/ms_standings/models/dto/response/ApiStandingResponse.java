package com.sportpulse.ms_standings.models.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiStandingResponse(
        @JsonProperty("response") List<ApiLeagueStanding> response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiLeagueStanding(
            @JsonProperty("league") ApiLeague league
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiLeague(
            Long id,
            String name,
            String country,
            Integer season,
            @JsonProperty("standings") List<List<ApiTeamStanding>> standings
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiTeamStanding(
            Integer rank,
            @JsonProperty("team") ApiTeam team,
            Integer points,
            @JsonProperty("all") ApiStats all,
            String form
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiTeam(
            Long id,
            String name,
            String logo
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiStats(
            Integer played,
            Integer win,
            Integer draw,
            Integer lose,
            @JsonProperty("goals") Goals goals
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Goals(
            @JsonProperty("for") Integer forGoals,
            @JsonProperty("against") Integer against
    ) {}
}