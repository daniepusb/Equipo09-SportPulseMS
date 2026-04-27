package com.sportpulse.ms_leagues.dto;

import java.util.List;

public record LeagueDetailResponse(
    Long                   id,
    String                 name,
    String                 type,
    String                 logo,
    String                 country,
    List<Integer>          seasons,
    LeagueCurrentSeason    currentSeason
) {}