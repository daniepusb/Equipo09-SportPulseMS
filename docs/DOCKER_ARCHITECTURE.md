# Arquitectura de Contenedores y Conectividad - SportPulse

Este documento detalla la configuración técnica de la infraestructura de microservicios basada en Docker, incluyendo el mapeo de puertos, la red interna y las dependencias de comunicación.

## Topologia de Red
Se utiliza una red interna tipo bridge llamada `sportpulse-internal-network`.
- **Aislamiento**: Los servicios de base de datos no exponen puertos al host, siendo accesibles exclusivamente de forma interna.
- **Resolucion de Nombres**: Los microservicios se comunican entre si utilizando el nombre del servicio definido en el archivo de orquestacion (ejemplo: `http://ms-auth:8081`).

## Inventario de Servicios y Puertos

| Servicio | Puerto Externo | Puerto Interno | Rol Principal |
| :--- | :---: | :---: | :--- |
| **ms-gateway** | 8080 | 8080 | Punto de entrada unico, ruteo y Rate Limiting. |
| **ms-auth** | 8081 | 8081 | Gestion de identidades, login y emision de tokens JWT. |
| **ms-leagues** | 8082 | 8082 | Gestion de ligas, paises y temporadas. |
| **ms-teams** | 8083 | 8083 | Informacion detallada de equipos y escudos. |
| **ms-fixtures** | 8084 | 8084 | Calendario de partidos y resultados en tiempo real. |
| **ms-standings** | 8085 | 8085 | Tablas de posiciones y estadisticas por temporada. |
| **ms-notifications** | 8086 | 8086 | Sistema de suscripciones y alertas de eventos. |
| **ms-dashboard** | 8087 | 8087 | Agregador de datos para vista ejecutiva. |
| **postgres-auth** | N/A | 5432 | Base de datos persistente para usuarios (PostgreSQL). |
| **postgres-notif** | N/A | 5432 | Base de datos para suscripciones (PostgreSQL). |

## Matriz de Conectividad Interna

| Origen | Destino | URL de Conexion | Proposito |
| :--- | :--- | :--- | :--- |
| **ms-gateway** | Todos | `http://ms-<servicio>:<puerto>` | Proxy inverso y ruteo de peticiones. |
| **ms-auth** | postgres-auth | `jdbc:postgresql://postgres-auth:5432/auth_db` | Persistencia de credenciales. |
| **ms-fixtures** | ms-teams | `http://ms-teams:8083` | Obtencion de informacion de equipos para partidos. |
| **ms-standings** | ms-teams | `http://ms-teams:8083` | Informacion de equipos para tablas de posiciones. |
| **ms-notifications** | ms-fixtures | `http://ms-fixtures:8084` | Monitoreo de partidos para disparar alertas. |
| **ms-notifications** | postgres-notif | `jdbc:postgresql://postgres-notifications:5432/notifications_db` | Registro de suscriptores. |
| **ms-dashboard** | ms-fixtures | `http://ms-fixtures:8084` | Datos de partidos recientes para el resumen. |
| **ms-dashboard** | ms-standings | `http://ms-standings:8085` | Datos de tablas para el resumen. |

## Configuracion de Desarrollo
- **Politica de Reinicio**: `restart: "no"`. Los contenedores no se inician automaticamente para optimizar el consumo de recursos en el entorno local.
- **Validacion de Estado (Healthchecks)**: Todos los microservicios implementan Spring Boot Actuator.
  - Endpoint de salud: `/actuator/health`
  - El Gateway posee una dependencia estricta (`depends_on`) que requiere que todos los servicios esten en estado "healthy" para su inicializacion.

## Variables de Entorno Criticas
- `RAPIDAPI_KEY`: Requerida para que los servicios de datos externos puedan realizar consultas exitosas.
- `JWT_SECRET`: Clave compartida para la firma y validacion de tokens de seguridad entre el servicio de autenticacion y el gateway.

---
*Ultima actualizacion: 14 de Abril, 2026*
