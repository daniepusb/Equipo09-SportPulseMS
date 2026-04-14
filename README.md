# Equipo09-SportPulseMS

## Project Description

SportPulse is a real-time football analytics platform composed of 8 microservices
that communicate with each other and consume data from the API-Football external API.

## Microservices

| Service          | Port | Responsibility                             |
|------------------|------|--------------------------------------------|
| ms-gateway       | 8080 | Punto de entrada, ruteo y rate limiting     |
| ms-auth          | 8081 | Registro, login y emisión de tokens JWT    |
| ms-leagues       | 8082 | Ligas, países y temporadas                 |
| ms-teams         | 8083 | Equipos, escudos e info general            |
| ms-fixtures      | 8084 | Partidos, calendarios y resultados         |
| ms-standings     | 8085 | Tablas de posiciones por temporada         |
| ms-notifications | 8086 | Suscripciones y alertas de eventos         |
| ms-dashboard     | 8087 | Resumen ejecutivo agregado                 |


