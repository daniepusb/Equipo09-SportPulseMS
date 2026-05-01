package com.sportpulse.ms_standings.service.impl;

import com.sportpulse.ms_standings.config.TeamsClient;
import com.sportpulse.ms_standings.models.dto.request.TeamDto;
import com.sportpulse.ms_standings.models.dto.response.ApiStandingResponse;
import com.sportpulse.ms_standings.models.dto.response.StandingResponse;
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
    @Cacheable(value = "standings", key = "#league + '-' + #season")
    public StandingResponse getStandings(Integer league, Integer season) {
                // 1. Obtener datos crudos de API-Football
                ApiStandingResponse apiResponse = standingRepository.getStandings(league, season);

                if (apiResponse == null || apiResponse.response() == null || apiResponse.response().isEmpty()) {
                    throw new RuntimeException("No standings found for league " + league + " season " + season);
                }

                // 2. Extraer la lista de equipos
                ApiStandingResponse.ApiLeague leagueData = apiResponse.response().get(0).league();
                List<List<ApiStandingResponse.ApiTeamStanding>> standingsLists = leagueData.standings();

                List<ApiStandingResponse.ApiTeamStanding> teamStandings = standingsLists.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

                // 3. Obtener todos los equipos de la liga de una vez para evitar múltiples llamadas
                Map<Long, TeamDto> teamsMap = teamsClient.getTeamsByLeagueAndSeason(league, season)
                        .stream()
                        .collect(Collectors.toMap(TeamDto::id, Function.identity(), (a, b) -> a));

                // 4. Enriquecer cada equipo usando el mapa local
                List<StandingResponse.TeamStanding> enrichedStandings = teamStandings.stream()
                        .map(ts -> {
                            TeamDto teamDto = teamsMap.get(ts.team().id());
                            
                            // Si por alguna razón no está en el mapa, usamos los datos básicos que ya tenemos
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
                                    ts.form()
                            );
                        })
                        .sorted(Comparator.comparingInt(StandingResponse.TeamStanding::rank))
                        .collect(Collectors.toList());

                // 5. Construir respuesta final
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
