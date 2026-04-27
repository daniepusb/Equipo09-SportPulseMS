package com.sportpulse.ms_fixtures.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.sportpulse.ms_fixtures.dto.TeamDto;

@FeignClient(
    name = "ms-teams",
    url = "${teams.service.url}"
)
public interface TeamsClient {
    @GetMapping("/api/teams/{id}")
    TeamDto getTeamById(@PathVariable("id") Long id);
}