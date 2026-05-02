package com.sportpulse.ms_standings.models.request;

public record TeamDto(
        Long id,
        String name,
        String logo,
        String country,
        Integer founded

) {
}
