package com.sportpulse.ms_fixtures.mapper;

import com.sportpulse.ms_fixtures.dto.FixtureResponse;
import com.sportpulse.ms_fixtures.model.Fixture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.sportpulse.ms_fixtures.model.TeamSnapshot;

@Mapper(componentModel = "spring")
public interface FixtureMapper {
    
    @Mapping(source = "statusShort", target = "status.shortCode")
    @Mapping(source = "statusLong", target = "status.longCode")
    @Mapping(source = "fixture.league.id", target = "league.id")
    @Mapping(source = "fixture.league.name", target = "league.name")
    @Mapping(source = "fixture.league.round", target = "league.round")
    @Mapping(target = "homeTeam", expression = "java(toTeamInfo(fixture.homeTeam(), fixture.goals() != null ? fixture.goals().home() : null, teamsData))")
    @Mapping(target = "awayTeam", expression = "java(toTeamInfo(fixture.awayTeam(), fixture.goals() != null ? fixture.goals().away() : null, teamsData))")
    @Mapping(source = "fixture.venue.name", target = "venue.name")
    @Mapping(source = "fixture.venue.city", target = "venue.city")
    FixtureResponse toResponse(Fixture fixture, Map<Long, TeamDto> teamsData);
    
    default FixtureResponse.TeamInfo toTeamInfo(TeamSnapshot team, Integer goals, Map<Long, TeamDto> teamsData) {
        if (team == null) return null;
        TeamDto enrichedTeam = teamsData.get(team.id());
        String logo = enrichedTeam != null ? enrichedTeam.logo() : team.logo();
        return new FixtureResponse.TeamInfo(team.id(), team.name(), logo, goals);
    }
}