package com.sportpulse.ms_leagues.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record League(
    Long          id,
    String        name,
    String        type,
    String        logo,
    String        country,
    List<Integer> seasons,
    Integer       currentSeason,
    LocalDate     startDate,
    LocalDate     endDate
) implements Serializable {
    private static final long serialVersionUID = 1L;
}