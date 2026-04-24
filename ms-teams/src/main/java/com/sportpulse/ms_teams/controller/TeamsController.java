package com.sportpulse.ms_teams.controller;

import java.util.List;
import com.sportpulse.ms_teams.dto.TeamResponse;
import com.sportpulse.ms_teams.mapper.TeamMapper;
import com.sportpulse.ms_teams.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {

    private final TeamService teamService;
    private final TeamMapper  teamMapper;

    public TeamsController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper  = teamMapper;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeamsByLeagueAndSeason(
        @RequestParam int league,
        @RequestParam int season,
        @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        requireUser(userId);
        List<TeamResponse> body = teamService.getTeamsByLeagueAndSeason(league, season)
            .stream()
            .map(teamMapper::toResponse)
            .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeamById(
        @PathVariable long teamId,
        @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        requireUser(userId);
        var team = teamService.getTeamById(teamId);
        if (team == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND");
        return ResponseEntity.ok(teamMapper.toResponse(team));
    }

    private void requireUser(String userId) {
        if (userId == null || userId.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user context");
    }
}