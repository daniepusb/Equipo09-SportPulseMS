package com.sportpulse.ms_standings.config;

import com.sportpulse.ms_standings.models.request.TeamDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "ms-teams",
        url = "${teams.service.url}",
        configuration = FeignAuthInterceptor.class
)
public interface TeamsClient {

    @GetMapping("/api/teams/{id}")
    TeamDto getTeamById(@PathVariable("id") Long id);

    @GetMapping("/api/teams")
    List<TeamDto> getTeamsByLeagueAndSeason(
            @RequestParam("league") Integer league,
            @RequestParam("season") Integer season
    );
}