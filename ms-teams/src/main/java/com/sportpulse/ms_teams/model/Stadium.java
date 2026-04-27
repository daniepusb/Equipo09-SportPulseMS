package com.sportpulse.ms_teams.model;

public record Stadium(
    String name,
    String address,
    String city,
    Integer capacity,
    String surface
) {}