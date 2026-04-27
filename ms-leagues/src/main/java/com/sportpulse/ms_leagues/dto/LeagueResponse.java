package com.sportpulse.ms_leagues.dto;

import java.time.LocalDate;

public record LeagueResponse(
    Long      id,
    String    name,
    String    type,
    String    logo,
    String    country,
    Integer   currentSeason,
    LocalDate startDate,
    LocalDate endDate
) {}