package com.sportpulse.ms_fixtures.controller;

import java.time.LocalDate;
import java.util.List;
import com.sportpulse.ms_fixtures.constants.ErrorMessages;
import com.sportpulse.ms_fixtures.constants.HeaderConstants;
import com.sportpulse.ms_fixtures.dto.FixtureResponse;
import com.sportpulse.ms_fixtures.model.FixtureStatus;
import com.sportpulse.ms_fixtures.service.FixtureService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/fixtures")
public class FixturesController {

    private final FixtureService fixtureService;

    public FixturesController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @GetMapping
    public ResponseEntity<List<FixtureResponse>> getFixtures(
        @RequestParam(required = false) Long league,
        @RequestParam(required = false) Integer season,
        @RequestParam(required = false) Long team,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) FixtureStatus status,
        @RequestHeader(value = HeaderConstants.X_USER_ID, required = false) String userId
    ) {
        requireUser(userId);
        List<FixtureResponse> body = fixtureService.getFixtures(league, season, team, date, status);
        return ResponseEntity.ok(body);
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessages.MISSING_AUTH_USER_CONTEXT);
    }
}