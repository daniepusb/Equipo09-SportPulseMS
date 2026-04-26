package com.sportpulse.ms_teams.model;

public record Team(
    Long    id,
    String  name,
    String  country,
    String  logo,
    Integer foundedYear,
    Boolean national,
    Stadium stadium
) {}