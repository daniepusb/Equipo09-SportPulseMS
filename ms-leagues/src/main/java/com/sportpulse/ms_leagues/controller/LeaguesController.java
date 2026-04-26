package com.sportpulse.ms_leagues.controller;

import java.util.List;
import com.sportpulse.ms_leagues.dto.LeagueDetailResponse;
import com.sportpulse.ms_leagues.dto.LeagueResponse;
import com.sportpulse.ms_leagues.mapper.LeagueMapper;
import com.sportpulse.ms_leagues.service.LeagueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/leagues")
@Tag(name = "Leagues", description = "League query operations")
public class LeaguesController {

    private final LeagueService leagueService;
    private final LeagueMapper  leagueMapper;

    public LeaguesController(LeagueService leagueService, LeagueMapper leagueMapper) {
        this.leagueService = leagueService;
        this.leagueMapper  = leagueMapper;
    }

    @Operation(
        summary = "List leagues",
        description = "Returns all available leagues, optionally filtered by country and season"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leagues retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LeagueResponse.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user",
            content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @GetMapping
    public ResponseEntity<List<LeagueResponse>> getLeagues(
        @RequestParam(required = false) String country,
        @RequestParam(required = false) Integer season,
        @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        requireUser(userId);
        List<LeagueResponse> body = leagueService.getAllLeagues(country, season)
            .stream()
            .map(leagueMapper::toResponse)
            .toList();
        return ResponseEntity.ok(body);
    }

    @Operation(
        summary = "Get league by ID",
        description = "Returns league information by its identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "League retrieved successfully",
            content = @Content(schema = @Schema(implementation = LeagueDetailResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user",
            content = @Content(schema = @Schema(implementation = Object.class))),
        @ApiResponse(responseCode = "404", description = "League not found",
            content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueDetailResponse> getLeagueById(
        @PathVariable long leagueId,
        @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        requireUser(userId);
        var league = leagueService.getLeagueById(leagueId);
        if (league == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LEAGUE_NOT_FOUND");
        return ResponseEntity.ok(leagueMapper.toDetailResponse(league));
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user context");
    }
}