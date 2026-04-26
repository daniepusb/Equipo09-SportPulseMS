package com.sportpulse.ms_leagues.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportpulse.ms_leagues.model.League;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ApiFootballLeagueRepository implements LeagueRepository {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiFootballBaseUrl;
    private final String apiFootballKey;
    private final String apiFootballHost;

    public ApiFootballLeagueRepository(
        @Value("${api-football.base-url}") String apiFootballBaseUrl,
        @Value("${api-football.key}")      String apiFootballKey,
        @Value("${api-football.host}")     String apiFootballHost
    ) {
        this.apiFootballBaseUrl = apiFootballBaseUrl;
        this.apiFootballKey     = apiFootballKey;
        this.apiFootballHost    = apiFootballHost;
    }

    @Override
    public List<League> findAll(String country, Integer season) {
        StringBuilder path = new StringBuilder("/leagues");
        List<Object> uriVars = new java.util.ArrayList<>();
        if (country != null && !country.isBlank()) {
            path.append(uriVars.isEmpty() ? "?" : "&").append("country={country}");
            uriVars.add(country);
        }
        if (season != null) {
            path.append(uriVars.isEmpty() ? "?" : "&").append("season={season}");
            uriVars.add(season);
        }
        ResponseEntity<ApiFootballLeaguesResponse> response = exchange(path.toString(), uriVars.toArray());
        return mapItems(response);
    }

    @Override
    public League findById(long leagueId) {
        ResponseEntity<ApiFootballLeaguesResponse> response = exchange(
            "/leagues?id={id}", leagueId
        );
        return mapItems(response).stream()
            .findFirst()
            .orElse(null);
    }

    private ResponseEntity<ApiFootballLeaguesResponse> exchange(String path, Object... uriVars) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-RapidAPI-Key",  apiFootballKey);
        headers.set("X-RapidAPI-Host", apiFootballHost);

        return restTemplate.exchange(
            apiFootballBaseUrl + path,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ApiFootballLeaguesResponse.class,
            uriVars
        );
    }

    private List<League> mapItems(ResponseEntity<ApiFootballLeaguesResponse> response) {
        if (response.getBody() == null || response.getBody().response() == null)
            return List.of();

        return response.getBody().response().stream()
            .filter(Objects::nonNull)
            .map(this::toDomain)
            .toList();
    }

    private League toDomain(ApiFootballLeagueItem item) {
        ApiFootballLeague l = item.league();
        String country = item.country() != null ? item.country().name() : null;
        List<ApiFootballSeason> seasonItems = item.seasons() != null ? item.seasons() : List.of();
        List<Integer> seasons = seasonItems.stream()
            .filter(s -> s.year() != null)
            .map(ApiFootballSeason::year)
            .toList();

        ApiFootballSeason currentSeasonItem = seasonItems.stream()
            .filter(s -> s.year() != null)
            .max((a, b) -> Integer.compare(a.year(), b.year()))
            .orElse(null);

        Integer currentSeason = currentSeasonItem != null ? currentSeasonItem.year() : null;
        LocalDate startDate = currentSeasonItem != null && currentSeasonItem.start() != null
            ? LocalDate.parse(currentSeasonItem.start()) : null;
        LocalDate endDate = currentSeasonItem != null && currentSeasonItem.end() != null
            ? LocalDate.parse(currentSeasonItem.end()) : null;

        return new League(
            l.id(),
            l.name(),
            l.type(),
            l.logo(),
            country,
            seasons,
            currentSeason,
            startDate,
            endDate
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballLeaguesResponse(
        @JsonProperty("response") List<ApiFootballLeagueItem> response
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballLeagueItem(
        @JsonProperty("league")  ApiFootballLeague     league,
        @JsonProperty("country") ApiFootballCountry    country,
        @JsonProperty("seasons") List<ApiFootballSeason> seasons
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballLeague(
        @JsonProperty("id")   Long   id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("logo") String logo
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballCountry(
        @JsonProperty("name") String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballSeason(
        @JsonProperty("year")  Integer year,
        @JsonProperty("start") String  start,
        @JsonProperty("end")   String  end
    ) {}
}