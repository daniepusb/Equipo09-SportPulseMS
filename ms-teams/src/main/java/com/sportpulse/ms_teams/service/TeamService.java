package com.sportpulse.ms_teams.service;

import java.util.List;
import com.sportpulse.ms_teams.model.Team;
import com.sportpulse.ms_teams.repository.TeamRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Cacheable(value = "teams", key = "#league + '-' + #season")
    public List<Team> getTeamsByLeagueAndSeason(int league, int season) {
        return teamRepository.findByLeagueAndSeason(league, season);
    }

    @Cacheable(value = "team", key = "#teamId")
    public Team getTeamById(long teamId) {
        return teamRepository.findById(teamId);
    }
}