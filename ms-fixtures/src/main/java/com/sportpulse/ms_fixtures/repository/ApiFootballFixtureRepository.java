package com.sportpulse.ms_fixtures.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportpulse.ms_fixtures.dto.ApiFootballEventResponse;
import com.sportpulse.ms_fixtures.model.Fixture;
import com.sportpulse.ms_fixtures.model.FixtureStatus;
import com.sportpulse.ms_fixtures.model.TeamSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ApiFootballFixtureRepository implements FixtureRepository {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiFootballBaseUrl;
    private final String apiFootballKey;
    private final String apiFootballHost;

    public ApiFootballFixtureRepository(
        @Value("${api-football.base-url}") String apiFootballBaseUrl,
        @Value("${api-football.key}")      String apiFootballKey,
        @Value("${api-football.host}")     String apiFootballHost
    ) {
        this.apiFootballBaseUrl = apiFootballBaseUrl;
        this.apiFootballKey     = apiFootballKey;
        this.apiFootballHost    = apiFootballHost;
    }

    public List<Fixture> findByFilters(Long league, Integer season, Long team, LocalDate date, FixtureStatus status) {
        StringBuilder urlBuilder = new StringBuilder(apiFootballBaseUrl + "/fixtures");
        boolean hasParams = false;

        if (league != null) {
            urlBuilder.append(hasParams ? "&" : "?").append("league=").append(league);
            hasParams = true;
        }
        if (season != null) {
            urlBuilder.append(hasParams ? "&" : "?").append("season=").append(season);
            hasParams = true;
        }
        if (team != null) {
            urlBuilder.append(hasParams ? "&" : "?").append("team=").append(team);
            hasParams = true;
        }
        if (date != null) {
            urlBuilder.append(hasParams ? "&" : "?").append("date=").append(date);
            hasParams = true;
        }
        if (status != null) {
            urlBuilder.append(hasParams ? "&" : "?").append("status=").append(status.name());
            hasParams = true;
        }

        String finalUrl = urlBuilder.toString();
        System.out.println("Calling API-Football: " + finalUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-RapidAPI-Key", apiFootballKey);
        headers.set("X-RapidAPI-Host", apiFootballHost);

        try {
            ResponseEntity<ApiFootballFixturesResponse> response = restTemplate.exchange(
            finalUrl,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ApiFootballFixturesResponse.class);

            if (response.getBody() == null || response.getBody().response() == null)
                return List.of();

            return response.getBody().response().stream()
                .filter(Objects::nonNull)
                .map(this::toDomain)
                .filter(Objects::nonNull)
                .toList();
        } catch (Exception e) {
            System.err.println("Error calling API-Football: " + e.getMessage());
            return List.of();
        }
    }

    private Fixture toDomain(ApiFootballFixtureItem item) {
        if (item.fixture() == null || item.league() == null || item.teams() == null)
            return null;

        TeamSnapshot homeTeam = toTeamSnapshot(item.teams().home());
        TeamSnapshot awayTeam = toTeamSnapshot(item.teams().away());
        if (homeTeam == null || awayTeam == null) return null;

        OffsetDateTime dateTime = item.fixture().date() != null
            ? OffsetDateTime.parse(item.fixture().date())
            : null;

        FixtureStatus statusShort = toFixtureStatus(item.fixture().status().shortCode());
        String statusLong = item.fixture().status().longCode();

        Fixture.LeagueInfo leagueInfo = new Fixture.LeagueInfo(
            item.league().id(),
            item.league().name(),
            item.league().round()
        );

        Fixture.VenueInfo venueInfo = item.venue() != null
            ? new Fixture.VenueInfo(item.venue().name(), item.venue().city())
            : new Fixture.VenueInfo(null, null);

        Fixture.GoalsInfo goalsInfo = item.goals() != null
            ? new Fixture.GoalsInfo(item.goals().home(), item.goals().away())
            : new Fixture.GoalsInfo(null, null);
        Integer elapsed = item.fixture().status().elapsed();

        return new Fixture(
            item.fixture().id(),
            dateTime,
            statusShort,
            statusLong,
            leagueInfo,
            homeTeam,
            awayTeam,
            venueInfo,
            goalsInfo,
            elapsed
        );
    }

    private TeamSnapshot toTeamSnapshot(ApiFootballTeam team) {
        if (team == null || team.id() == null) return null;
        return new TeamSnapshot(team.id(), team.name(), team.logo());
    }

    private FixtureStatus toFixtureStatus(String shortCode) {
        if (shortCode == null) return FixtureStatus.NS;
        return switch (shortCode) {
            case "FT", "AET", "PEN" -> FixtureStatus.FT;
            case "LIVE", "1H", "HT", "2H", "ET", "BT", "P", "SUSP", "INT" -> FixtureStatus.LIVE;
            case "NS", "TBD" -> FixtureStatus.NS;
            default -> FixtureStatus.NS;
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballFixturesResponse(
        @JsonProperty("response") List<ApiFootballFixtureItem> response
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballFixtureItem(
        @JsonProperty("fixture") ApiFootballFixture fixture,
        @JsonProperty("league") ApiFootballLeague league,
        @JsonProperty("teams") ApiFootballTeams teams,
        @JsonProperty("goals") ApiFootballGoals goals,
        @JsonProperty("venue") ApiFootballVenue venue
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballFixture(
        @JsonProperty("id") Long id,
        @JsonProperty("date") String date,
        @JsonProperty("status") ApiFootballFixtureStatus status
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballFixtureStatus(
        @JsonProperty("short") String shortCode,
        @JsonProperty("long") String longCode,
        @JsonProperty("elapsed") Integer elapsed
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballLeague(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("round") String round
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballTeams(
        @JsonProperty("home") ApiFootballTeam home,
        @JsonProperty("away") ApiFootballTeam away
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballTeam(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("logo") String logo
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballGoals(
        @JsonProperty("home") Integer home,
        @JsonProperty("away") Integer away
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ApiFootballVenue(
        @JsonProperty("name") String name,
        @JsonProperty("city") String city
    ) {}


    public List<ApiFootballEventResponse> getEventsByFixtureId(Integer fixtureId) {
        String url = apiFootballBaseUrl + "/fixtures/events?fixture=" + fixtureId;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-RapidAPI-Key", apiFootballKey);
        headers.set("X-RapidAPI-Host", apiFootballHost);

        ResponseEntity<ApiFootballEventsResponse> response = restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers), ApiFootballEventsResponse.class);

        return response.getBody() != null ? response.getBody().response() : List.of();
    }


    private record ApiFootballEventsResponse(
        @JsonProperty("response") List<ApiFootballEventResponse> response
    ) {}
}