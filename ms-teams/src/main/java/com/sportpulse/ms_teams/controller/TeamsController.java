package com.sportpulse.ms_teams.controller;

import java.util.List;
import com.sportpulse.ms_teams.constants.ApiPaths;
import com.sportpulse.ms_teams.constants.ErrorCodes;
import com.sportpulse.ms_teams.constants.ErrorMessages;
import com.sportpulse.ms_teams.constants.HeaderConstants;
import com.sportpulse.ms_teams.dto.TeamResponse;
import com.sportpulse.ms_teams.mapper.TeamMapper;
import com.sportpulse.ms_teams.service.TeamService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(ApiPaths.TEAMS_BASE)
@Tag(name = "Teams", description = "Team query operations")
public class TeamsController {

    private final TeamService teamService;
    private final TeamMapper  teamMapper;

    public TeamsController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper  = teamMapper;
    }

    @Operation(
        summary = "List teams by league and season",
        description = "Returns teams associated with a specific league and season"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teams retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeamResponse.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid or missing parameters",
            content = @Content(schema = @Schema(implementation = Object.class))),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user",
            content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeamsByLeagueAndSeason(
        @RequestParam int league,
        @RequestParam int season,
        @RequestHeader(value = HeaderConstants.X_USER_ID, required = false) String userId
    ) {
        requireUser(userId);
        List<TeamResponse> body = teamService.getTeamsByLeagueAndSeason(league, season)
            .stream()
            .map(teamMapper::toResponse)
            .toList();
        return ResponseEntity.ok(body);
    }

    @Operation(
        summary = "Get team by ID",
        description = "Returns team information by its identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Team retrieved successfully",
            content = @Content(schema = @Schema(implementation = TeamResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthenticated user",
            content = @Content(schema = @Schema(implementation = Object.class))),
        @ApiResponse(responseCode = "404", description = "Team not found",
            content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeamById(
        @PathVariable long teamId,
        @RequestHeader(value = HeaderConstants.X_USER_ID, required = false) String userId
    ) {
        requireUser(userId);
        var team = teamService.getTeamById(teamId);
        if (team == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCodes.TEAM_NOT_FOUND);
        return ResponseEntity.ok(teamMapper.toResponse(team));
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessages.MISSING_AUTH_USER_CONTEXT);
    }
}