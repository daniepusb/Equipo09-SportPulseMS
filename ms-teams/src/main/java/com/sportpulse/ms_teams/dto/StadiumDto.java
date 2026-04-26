package com.sportpulse.ms_teams.dto;

public record StadiumDto(
    String  name,
    String  address,
    String  city,
    Integer capacity,
    String  surface
) {}