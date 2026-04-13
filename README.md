# Equipo09-SportPulseMS

## Project Description

SportPulse is a real-time football analytics platform composed of 8 microservices
that communicate with each other and consume data from the API-Football external API.

## Microservices

| Service          | Port | Responsibility                             |
|------------------|------|--------------------------------------------|
| ms-gateway       | 8080 | Entry point, routing and rate limiting     |
| ms-auth          | 8081 | Registration, login and JWT token issuance |
| ms-leagues       | 8082 | Leagues, countries and seasons             |
| ms-teams         | 8083 | Teams, shields and general info            |
| ms-fixtures      | 8085 | Matches, calendars and results             |
| ms-standings     | 8086 | League standings by season                 |
| ms-notifications | 8088 | Subscriptions and event alerts             |
| ms-dashboard     | 8089 | Aggregated executive summary               |


