package com.sportpulse.ms_leagues.service;

import java.util.List;
import com.sportpulse.ms_leagues.model.League;
import com.sportpulse.ms_leagues.repository.LeagueRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;

    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    @Cacheable(value = "leagues", key = "#country + ':' + #season")
    public List<League> getAllLeagues(String country, Integer season) {
        return leagueRepository.findAll(country, season);
    }

    @Cacheable(value = "leagues", key = "#leagueId")
    public League getLeagueById(long leagueId) {
        return leagueRepository.findById(leagueId);
    }
}