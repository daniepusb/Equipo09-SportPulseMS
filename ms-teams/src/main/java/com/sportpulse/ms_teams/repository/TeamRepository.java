package com.sportpulse.ms_teams.repository;

import java.util.List;
import com.sportpulse.ms_teams.model.Team;

public interface TeamRepository {
    List<Team> findByLeagueAndSeason(int league, int season);
    Team       findById(long teamId);
}