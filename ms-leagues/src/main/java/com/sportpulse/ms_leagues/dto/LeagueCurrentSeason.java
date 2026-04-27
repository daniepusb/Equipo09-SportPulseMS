package com.sportpulse.ms_leagues.dto;

import java.time.LocalDate;

public record LeagueCurrentSeason(
    Integer   year,
    LocalDate startDate,
    LocalDate endDate,
    Boolean   current
) {}