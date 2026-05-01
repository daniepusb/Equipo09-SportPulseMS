package com.sportpulse.ms_standings.controller;

import com.sportpulse.ms_standings.models.dto.response.StandingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/standings")
public interface StandingsApi {

    @GetMapping
    ResponseEntity<StandingResponse> getStandings(
            @RequestParam Integer league,
            @RequestParam Integer season,
            @RequestHeader(value = "X-User-Id", required = false) String userId
    );

}
