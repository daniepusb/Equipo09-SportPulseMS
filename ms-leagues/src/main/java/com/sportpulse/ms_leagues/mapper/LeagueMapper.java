package com.sportpulse.ms_leagues.mapper;

import com.sportpulse.ms_leagues.dto.LeagueCurrentSeason;
import com.sportpulse.ms_leagues.dto.LeagueDetailResponse;
import com.sportpulse.ms_leagues.dto.LeagueResponse;
import com.sportpulse.ms_leagues.model.League;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeagueMapper {

    LeagueResponse toResponse(League league);

    @Mapping(target = "currentSeason", expression = "java(toCurrentSeason(league))")
    LeagueDetailResponse toDetailResponse(League league);

    default LeagueCurrentSeason toCurrentSeason(League league) {
        if (league == null || league.currentSeason() == null)
            return null;
        return new LeagueCurrentSeason(
            league.currentSeason(),
            league.startDate(),
            league.endDate(),
            Boolean.TRUE
        );
    }
}