package com.sportpulse.ms_standings.controller.impl;

import com.sportpulse.ms_standings.controller.StandingsApi;
import com.sportpulse.ms_standings.models.dto.response.StandingResponse;
import com.sportpulse.ms_standings.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class StandingApiController implements StandingsApi {

    private final StandingService standingService;


    @Override
    public ResponseEntity<StandingResponse> getStandings(Integer league, Integer season, String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "userId is required");
        }
        StandingResponse response = standingService.getStandings(league, season);
        return ResponseEntity.ok(response);
    }
}
