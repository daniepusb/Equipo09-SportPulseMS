package com.sportpulse.ms_teams.mapper;

import com.sportpulse.ms_teams.dto.TeamResponse;
import com.sportpulse.ms_teams.model.Team;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "foundedYear", target = "founded")
    @Mapping(source = "stadium",     target = "stadium")
    TeamResponse toResponse(Team team);
}