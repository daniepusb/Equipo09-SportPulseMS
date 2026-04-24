# ⚽ SportPulse

Real-time football analytics platform built with a microservices architecture. It consumes data from [API-Football (RapidAPI)](https://rapidapi.com/apisports/api/api-football) and provides a complete system for authentication, leagues, teams, fixtures, standings, notifications, and an aggregated dashboard.

For Docker topology, service ports, and internal connectivity, see [./docs/DOCKER_ARCHITECTURE.md](docs/DOCKER_ARCHITECTURE.md). The Docker information lives in /docs/DOCKER_ARCHITECTURE.md.

---

## Team Members 🫂

@daniepusb  
@dio-quincarDev  
@EmanuelTitoDev  
@lopezsDev  
@karlosvas

---

### Functional Requirements (FR) 📋

| ID    | Requirement                                     | Microservice     |
| ----- | ----------------------------------------------- | ---------------- |
| FR-01 | User registration with unique email validation  | ms-auth          |
| FR-02 | Authentication and JWT token issuance           | ms-auth          |
| FR-03 | League listing filterable by country/season     | ms-leagues       |
| FR-04 | Team lookup by league/season                    | ms-teams         |
| FR-05 | Real-time fixture visualization                 | ms-fixtures      |
| FR-06 | Standings by league/season                      | ms-standings     |
| FR-07 | Subscription to event notifications             | ms-notifications |
| FR-08 | Aggregated dashboard with multiple data sources | ms-dashboard     |
| FR-09 | IP-based rate limiting at the gateway           | ms-gateway       |

### Non-Functional Requirements (NFR)

| ID     | Requirement               | Target metric                                    |
| ------ | ------------------------- | ------------------------------------------------ |
| NFR-01 | System availability       | 99.9% (excluding the external API)               |
| NFR-02 | Response time (p95)       | < 500ms for endpoints without external API calls |
| NFR-03 | Partial failure tolerance | Circuit breakers in service-to-service calls     |
| NFR-04 | Security                  | JWT tokens with expiration, HTTPS in production  |
| NFR-05 | Horizontal scalability    | Each microservice can run with multiple replicas |
| NFR-06 | External API consumption  | Max 100 requests/day (free plan)                 |
| NFR-07 | Cache                     | 5-minute TTL for API-Football data               |
| NFR-08 | Traceability              | Correlation ID on every request                  |

| Microservice       | Port | Responsibility                              |
| ------------------ | ---- | ------------------------------------------- |
| `ms-gateway`       | 8080 | Entry point, routing, and rate limiting     |
| `ms-auth`          | 8081 | Registration, login, and JWT token issuance |
| `ms-leagues`       | 8082 | Leagues, countries, and seasons             |
| `ms-teams`         | 8083 | Teams, badges, and general information      |
| `ms-fixtures`      | 8085 | Fixtures, schedules, and results            |
| `ms-standings`     | 8086 | Standings by league and season              |
| `ms-notifications` | 8088 | Subscriptions and event alerts              |
| `ms-dashboard`     | 8089 | Aggregated executive summary                |

## 🧠 Architectural Decisions (ADR)

| Area                    | Chosen decision                 | Alternatives                  | Rationale                                                       |
| ----------------------- | ------------------------------- | ----------------------------- | --------------------------------------------------------------- |
| **Architecture style**  | Microservices                   | Monolith, Serverless          | Independent scalability (notifications requires more resources) |
| **Communication**       | Synchronous (Feign)             | Async (RabbitMQ/Kafka)        | Initial simplicity, low need for eventual consistency           |
| **Database**            | PostgreSQL per service          | Shared single DB, MongoDB     | Schema independence, avoids bottlenecks                         |
| **Cache**               | Caffeine (in-memory)            | Redis, Memcached              | Simplicity, each service owns its data independently            |
| **Authentication**      | Stateless JWT                   | OAuth2 with Keycloak, Session | Lower operational overhead, easy horizontal scaling             |
| **Rate limiting**       | Gateway (Bucket4j)              | Per service, Nginx            | Single control point, prevents malicious traffic                |
| **Failure handling**    | Circuit breakers (Resilience4J) | Retries, Timeouts             | Prevents failure cascades between services                      |
| **External API access** | Feign + Cache                   | WebClient, RestTemplate       | Feign is declarative, cache minimizes consumption               |

---

## Technology Stack

- **Java 17** + **Spring Boot 3.x**
- **Spring Security** + **JWT** for authentication
- **PostgreSQL** — independent database per service (where applicable)
- **OpenFeign** for service-to-service communication
- **Resilience4J** for circuit breakers
- **MapStruct** + **Lombok** to reduce boilerplate
- **Swagger UI / OpenAPI 3.0** — available at `/swagger-ui` for each service
- **JUnit 5** + **Mockito** for testing
- **Docker** + **Docker Compose** for orchestration

---

## Prerequisites

- Docker >= 24 and Docker Compose v2
- JDK 17 (only if you want to build without Docker)
- Maven or Gradle
- API key for [RapidAPI — API-Football](https://rapidapi.com/apisports/api/api-football)

---

## Installation and Startup

### Clone the repository

```bash
git clone https://github.com/<your-username>/Equipo09-SportPulseMS.git
cd sportpulse
```

### 2. Configure environment variables

Create a `.env` file by copying from `.env.example`:

```bash
cp .env.example .env
```

Annd fill with your values.

## Main Endpoints

Once up, all traffic enters through the gateway at `http://localhost:8080`.

### Authentication

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/validate
```

All other endpoints require the header:

```
Authorization: Bearer <token>
```

### Leagues

```http
GET /api/leagues?country=Spain&season=2024
GET /api/leagues/{leagueId}
```

### Teams

```http
GET /api/teams?league=140&season=2024
GET /api/teams/{teamId}
```

### Fixtures

```http
GET /api/fixtures?league=140&status=NS
GET /api/fixtures/live
GET /api/fixtures/{fixtureId}/events
```

### Standings

```http
GET /api/standings?league=140&season=2024
GET /api/standings/team/{teamId}?league=140&season=2024
```

### Notifications

```http
POST   /api/notifications/subscribe
GET    /api/notifications/subscriptions
DELETE /api/notifications/subscribe/{subscriptionId}
```

### Dashboard

```http
GET /api/dashboard?league=140&season=2024
```

### Health check

```http
GET /health
```

---

## Swagger Documentation

Each microservice exposes its own OpenAPI documentation:

| Service          | URL                                                                  |
| ---------------- | -------------------------------------------------------------------- |
| ms-auth          | [http://localhost:8081/swagger-ui](http://localhost:8081/swagger-ui) |
| ms-leagues       | [http://localhost:8082/swagger-ui](http://localhost:8082/swagger-ui) |
| ms-teams         | [http://localhost:8083/swagger-ui](http://localhost:8083/swagger-ui) |
| ms-fixtures      | [http://localhost:8085/swagger-ui](http://localhost:8085/swagger-ui) |
| ms-standings     | [http://localhost:8086/swagger-ui](http://localhost:8086/swagger-ui) |
| ms-notifications | [http://localhost:8088/swagger-ui](http://localhost:8088/swagger-ui) |
| ms-dashboard     | [http://localhost:8089/swagger-ui](http://localhost:8089/swagger-ui) |

> ⚠️ In production, Swagger access would be disabled for security, but it is useful for development and testing.

### Testing

```bash
# All services
mvn clean verify

# Specific service with report
cd ms-auth
mvn jacoco:report
# Report at target/site/jacoco/index.html
```

---

## Rate Limiting

The gateway applies a limit of **60 requests/minute per IP**. When it is exceeded:

```json
{
  "error": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests. Limit: 60 req/min",
  "retryAfter": 30,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Repository Structure

```
sportpulse/
├── ms-gateway/
├── ms-auth/
├── ms-leagues/
├── ms-teams/
├── ms-fixtures/
├── ms-standings/
├── ms-notifications/
├── ms-dashboard/
├── postman/
│   └── SportPulse.postman_collection.json
├── docker-compose.yml
├── .env                  # not included in the repo
└── README.md
```

## Postman Collection

Import `postman/SportPulse.postman_collection.json` into Postman.  
Set the environment variable `base_url` to `http://localhost:8080` and `token` to the JWT obtained from `/api/auth/login`.

## 📝 License

This project is academic and has no commercial-use license.
