# api-gateway

Punto de entrada unico del frontend hacia el backend del Libro de Clases.

Enruta todas las peticiones `/api/**` al BFF (`bff-libroclases`) usando Eureka + LoadBalancer. El frontend ya no debe llamar al puerto 8083 directamente.

## Puerto

- API Gateway: `http://localhost:8080`
- API base para el frontend: `http://localhost:8080/api/v1`
- Health: `http://localhost:8080/actuator/health`
- Rutas configuradas: `http://localhost:8080/actuator/gateway/routes`

## Orden de arranque

1. MySQL (`docker compose up -d` en `fsk3-bff`)
2. Eureka (`fsk3-eureka-server`, puerto 8761)
3. `ms-academico` (8081)
4. `ms-asistencia` (8082)
5. `bff-libroclases` (8083)
6. **api-gateway** (8080)
7. Frontend (5173)

## Ejecutar

```powershell
cd fsk3-api-gateway
.\mvnw.cmd spring-boot:run
```

Git Bash:

```bash
./mvnw spring-boot:run
```

## Que hace el Gateway

- **Enrutamiento:** `Path=/api/**` -> `lb://bff-libroclases`
- **CORS:** permite peticiones desde `http://localhost:5173`
- **Filtro global:** `RequestLoggingGlobalFilter` registra metodo, path, si trae JWT y codigo HTTP de respuesta
- **Seguridad:** el JWT se valida en el BFF; el Gateway solo reenvia el header `Authorization`

## Flujo

```text
Frontend (5173) -> API Gateway (8080) -> BFF (8083) -> ms-academico / ms-asistencia
                              |
                         Eureka (8761)
```

## Tests

```powershell
.\mvnw.cmd test
```
