package com.sportpulse.ms_teams.dto;

public record TeamResponse(
    Long       id,
    String     name,
    String     country,
    String     logo,
    Integer    founded,
    Boolean    national,
    StadiumDto stadium
) {}