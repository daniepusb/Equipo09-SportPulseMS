# SportPulse Microservices Gateway

El Gateway actúa como el punto de entrada único para todos los microservicios del ecosistema SportPulse, gestionando el enrutamiento, la limitación de tasa (rate limiting) y el manejo centralizado de errores.

## 🚀 Enrutamiento y Puertos

El gateway redirige las peticiones según el prefijo del path hacia los microservicios correspondientes:

| Prefijo Path | Servicio Destino | Puerto Interno/Default |
| :--- | :--- | :--- |
| `/api/auth/**` | `ms-auth` | 8081 |
| `/api/leagues/**` | `ms-leagues` | 8082 |
| `/api/teams/**` | `ms-teams` | 8083 |
| `/api/fixtures/**` | `ms-fixtures` | 8085 |
| `/api/standings/**` | `ms-standings` | 8086 |
| `/api/notifications/**` | `ms-notifications` | 8088 |
| `/api/dashboard/**` | `ms-dashboard` | 8089 |

## 🛠️ Características Principales

- **Respuesta Idéntica:** El cliente recibe exactamente la misma respuesta (cuerpo y headers) que devuelve el microservicio destino.
- **Manejo de Error 503:** Si un microservicio destino no está disponible (ej. caída de red o servicio apagado), el Gateway responde automáticamente con un código **503 Service Unavailable** y un mensaje de error claro en formato JSON.
- **Limitación de Tasa:** Implementa `RateLimitFilter` utilizando Redis para proteger el sistema contra abusos.

## 🐳 Pruebas con Docker

Para realizar pruebas rápidas levantando únicamente el Gateway y un servicio de prueba (sin arrancar todo el ecosistema):

### Levantar solo Gateway y Leagues
```bash
docker compose up -d --no-deps ms-gateway ms-leagues
```

### Probar Redirección (OK)
```bash
curl -i http://localhost:8080/api/leagues/test
```

### Probar Error 503 (Servicio Caído)
Si intentas acceder a un servicio que no has levantado (ej. Auth):
```bash
curl -i http://localhost:8080/api/auth/test
```

---
*Desarrollado para el proyecto SportPulse.*
