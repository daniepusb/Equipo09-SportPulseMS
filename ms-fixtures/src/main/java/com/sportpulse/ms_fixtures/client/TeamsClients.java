package com.sportpulse.ms_fixtures.client;

@FeignClient(
    name = "ms-teams",
    url = "${teams.service.url}"
)
public interface TeamsClient {
    @GetMapping("/api/teams/{id}")
    TeamDto getTeamById(@PathVariable("id") Long id);
}