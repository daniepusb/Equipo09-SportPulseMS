package com.sportpulse.ms_leagues.repository;

import java.util.List;
import com.sportpulse.ms_leagues.model.League;

public interface LeagueRepository {
    List<League> findAll(String country, Integer season);
    League       findById(long leagueId);
}