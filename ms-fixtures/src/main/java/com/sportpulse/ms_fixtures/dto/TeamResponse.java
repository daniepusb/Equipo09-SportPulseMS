package com.sportpulse.ms_fixtures.dto;

public record TeamResponse(
    Long id,
    String name,
    String logo,
    Integer goals
) {}