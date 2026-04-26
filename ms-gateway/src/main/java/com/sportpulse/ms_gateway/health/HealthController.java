package com.sportpulse.ms_gateway.health;

import com.sportpulse.ms_gateway.constants.HealthConstants;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authUrl;
    @Value("${LEAGUES_SERVICE_URL:http://localhost:8082}")
    private String leaguesUrl;
    @Value("${TEAMS_SERVICE_URL:http://localhost:8083}")
    private String teamsUrl;
    @Value("${FIXTURES_SERVICE_URL:http://localhost:8084}")
    private String fixturesUrl;
    @Value("${STANDINGS_SERVICE_URL:http://localhost:8085}")
    private String standingsUrl;
    @Value("${NOTIFICATIONS_SERVICE_URL:http://localhost:8086}")
    private String notificationsUrl;
    @Value("${DASHBOARD_SERVICE_URL:http://localhost:8087}")
    private String dashboardUrl;

    private final WebClient webClient = WebClient.create();

    @GetMapping
    public Mono<Map<String, Object>> health() {
        return Mono.zip(
            check(authUrl), check(leaguesUrl), check(teamsUrl),
            check(fixturesUrl), check(standingsUrl), check(notificationsUrl),
            check(dashboardUrl)
        ).map(tuple -> {
            Map<String, Object> response = new HashMap<>();
            response.put(HealthConstants.KEY_GATEWAY, HealthConstants.STATUS_UP);
            response.put(HealthConstants.KEY_TIMESTAMP, Instant.now());

            Map<String, String> services = new HashMap<>();
            services.put("ms-auth", tuple.getT1());
            services.put("ms-leagues", tuple.getT2());
            services.put("ms-teams", tuple.getT3());
            services.put("ms-fixtures", tuple.getT4());
            services.put("ms-standings", tuple.getT5());
            services.put("ms-notifications", tuple.getT6());
            services.put("ms-dashboard", tuple.getT7());

            response.put(HealthConstants.KEY_SERVICES, services);
            return response;
        });
    }

    private Mono<String> check(String url) {
        return webClient.get()
            .uri(url + HealthConstants.ACTUATOR_HEALTH_PATH)
            .retrieve()
            .toBodilessEntity()
            .map(response -> HealthConstants.STATUS_UP)
            .timeout(java.time.Duration.ofSeconds(2))
            .onErrorReturn(HealthConstants.STATUS_DOWN);
    }
}
