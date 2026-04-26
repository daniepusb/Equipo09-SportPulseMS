package com.sportpulse.ms_teams.repository;

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportpulse.ms_teams.model.Stadium;
import com.sportpulse.ms_teams.model.Team;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ApiFootballTeamRepository implements TeamRepository {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiFootballBaseUrl;
    private final String apiFootballKey;
    private final String apiFootballHost;

    public ApiFootballTeamRepository(
        @Value("${api-football.base-url}") String apiFootballBaseUrl,
        @Value("${api-football.key}")      String apiFootballKey,
        @Value("${api-football.host}")     String apiFootballHost
    ) {
        this.apiFootballBaseUrl = apiFootballBaseUrl;
        this.apiFootballKey     = apiFootballKey;
        this.apiFootballHost    = apiFootballHost;
    }

    @Override
    public List<Team> findByLeagueAndSeason(int league, int season) {
        ResponseEntity<ApiFootballTeamsResponse> response = exchange(
            "/teams?league={league}&season={season}", league, season
        );
        return mapItems(response);
    }

    @Override
    public Team findById(long teamId) {
        ResponseEntity<ApiFootballTeamsResponse> response = exchange(
            "/teams?id={id}", teamId
        );
        return mapItems(response).stream()
            .findFirst()
            .orElse(null);
    }

    private ResponseEntity<ApiFootballTeamsResponse> exchange(String path, Object... uriVars) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-RapidAPI-Key",  apiFootballKey);
        headers.set("X-RapidAPI-Host", apiFootballHost);

        return restTemplate.exchange(
            apiFootballBaseUrl + path,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ApiFootballTeamsResponse.class,
            uriVars
        );
    }

    private List<Team> mapItems(ResponseEntity<ApiFootballTeamsResponse> response) {
        if (response.getBody() == null || response.getBody().response() == null)
            return List.of();

        return response.getBody().response().stream()
            .filter(Objects::nonNull)
            .map(this::toDomain)
            .toList();
    }

    private Team toDomain(ApiFootballTeamItem item) {
        ApiFootballVenue v = item.venue();
        Stadium stadium = new Stadium(
            v != null ? v.name()     : null,
            v != null ? v.address()  : null,
            v != null ? v.city()     : null,
            v != null ? v.capacity() : null,
            v != null ? v.surface()  : null
        );

        ApiFootballTeam t = item.team();
        return new Team(
            t.id(),
            t.name(),
            t.country(),
            t.logo(),
            t.founded(),
            t.national(),
            stadium
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballTeamsResponse(
        @JsonProperty("response") List<ApiFootballTeamItem> response
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballTeamItem(
        @JsonProperty("team")  ApiFootballTeam  team,
        @JsonProperty("venue") ApiFootballVenue venue
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballTeam(
        @JsonProperty("id")       Long    id,
        @JsonProperty("name")     String  name,
        @JsonProperty("country")  String  country,
        @JsonProperty("logo")     String  logo,
        @JsonProperty("founded")  Integer founded,
        @JsonProperty("national") Boolean national
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballVenue(
        @JsonProperty("name")     String  name,
        @JsonProperty("address")  String  address,
        @JsonProperty("city")     String  city,
        @JsonProperty("capacity") Integer capacity,
        @JsonProperty("surface")  String  surface
    ) {}
}