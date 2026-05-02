package com.sportpulse.ms_teams.model;

import java.io.Serializable;

public record Team(
    Long    id,
    String  name,
    String  country,
    String  logo,
    Integer foundedYear,
    Boolean national,
    Stadium stadium
) implements Serializable {}