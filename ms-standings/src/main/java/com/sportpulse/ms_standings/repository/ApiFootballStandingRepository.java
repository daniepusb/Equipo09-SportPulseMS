package com.sportpulse.ms_standings.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportpulse.ms_standings.models.response.ApiStandingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ApiFootballStandingRepository implements StandingRepository {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiFootballBaseUrl;
    private final String apiFootballKey;

    public ApiFootballStandingRepository(
            @Value("${api-football.base-url}") String apiFootballBaseUrl,
            @Value("${api-football.key}") String apiFootballKey
    ) {
        this.apiFootballBaseUrl = apiFootballBaseUrl;
        this.apiFootballKey = apiFootballKey;
    }

    public ApiStandingResponse getStandings(Integer league, Integer season) {
        String url = apiFootballBaseUrl + "/standings?league=" + league + "&season=" + season;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", apiFootballKey);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            System.out.println("API-Football Response: " + response.getBody());
            return objectMapper.readValue(response.getBody(), ApiStandingResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse standings response", e);
        }
    }

}