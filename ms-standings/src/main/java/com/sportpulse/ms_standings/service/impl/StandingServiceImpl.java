package com.sportpulse.ms_standings.service.impl;

import com.sportpulse.ms_standings.config.TeamsClient;
import com.sportpulse.ms_standings.models.request.TeamDto;
import com.sportpulse.ms_standings.models.response.ApiStandingResponse;
import com.sportpulse.ms_standings.models.response.StandingResponse;
import com.sportpulse.ms_standings.repository.StandingRepository;
import com.sportpulse.ms_standings.service.StandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingServiceImpl implements StandingService {

    private final StandingRepository standingRepository;
    private final TeamsClient teamsClient;

    @Override
    public StandingResponse getStandings(Integer league, Integer season) {
        return loadAllStandings(league, season);
    }

    @Override
    public StandingResponse getTeamStanding(Long teamId, Integer league, Integer season) {
        StandingResponse allStandings = loadAllStandings(league, season);

        List<StandingResponse.TeamStanding> filtered = allStandings.standings().stream()
                .filter(ts -> ts.team().id().equals(teamId))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            throw new RuntimeException("Team not found in standings: " + teamId);
        }

        return new StandingResponse(allStandings.league(), filtered);
    }

    @Cacheable(value = "standings", key = "#league + '-' + #season")
    private StandingResponse loadAllStandings(Integer league, Integer season) {
        ApiStandingResponse apiResponse = standingRepository.getStandings(league, season);

        if (apiResponse == null || apiResponse.response() == null || apiResponse.response().isEmpty()) {
            throw new RuntimeException("No standings found for league " + league + " season " + season);
        }

        ApiStandingResponse.ApiLeague leagueData = apiResponse.response().get(0).league();
        List<List<ApiStandingResponse.ApiTeamStanding>> standingsLists = leagueData.standings();

        List<ApiStandingResponse.ApiTeamStanding> teamStandings = standingsLists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Map<Long, TeamDto> teamsMap = teamsClient.getTeamsByLeagueAndSeason(league, season)
                .stream()
                .collect(Collectors.toMap(TeamDto::id, Function.identity(), (a, b) -> a));

        List<StandingResponse.TeamStanding> enrichedStandings = teamStandings.stream()
                .map(ts -> {
                    TeamDto teamDto = teamsMap.get(ts.team().id());

                    String name = (teamDto != null) ? teamDto.name() : ts.team().name();
                    String logo = (teamDto != null) ? teamDto.logo() : ts.team().logo();

                    StandingResponse.TeamInfo teamInfo = new StandingResponse.TeamInfo(
                            ts.team().id(),
                            name,
                            logo
                    );

                    return new StandingResponse.TeamStanding(
                            ts.rank(),
                            teamInfo,
                            ts.points(),
                            ts.all().played(),
                            ts.all().win(),
                            ts.all().draw(),
                            ts.all().lose(),
                            ts.all().goals().forGoals(),
                            ts.all().goals().against(),
                            ts.all().goals().forGoals() - ts.all().goals().against(),
                            ts.form(),
                            ts.description()
                    );
                })
                .sorted(Comparator.comparingInt(StandingResponse.TeamStanding::rank))
                .collect(Collectors.toList());

        return new StandingResponse(
                new StandingResponse.LeagueInfo(
                        leagueData.id(),
                        leagueData.name(),
                        leagueData.country(),
                        leagueData.season()
                ),
                enrichedStandings
        );
    }

}