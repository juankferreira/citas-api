# Validación S3

Fecha de actualización: 2026-09-25.

## Alcance comprobado en código

- Afiliación opcional durante el registro: catálogo de planes activos, registro sin plan, registro con `insurancePlanId`, FK en `user_insurance_affiliations` y rechazo de planes inexistentes o inactivos sin usuario parcial.
- Reserva concurrente: bloqueo transaccional de slots, una sola reserva válida y conflicto para el segundo intento; cobertura tanto para `APPROVED` (general) como `REQUESTED` (especializada).
- Zona de negocio: el `Clock` de la aplicación usa `America/Bogota`.

## Ejecuciones registradas

| Fecha | Repositorio | Comando | Resultado |
|---|---|---|---|
| 2026-09-25 | `citas-web` | `npm run lint` | PASS — TypeScript sin errores. |
| 2026-09-25 | `citas-web` | `npm test` | PASS — 3 archivos y 10 pruebas Vitest. Incluye registro mínimo y registro con plan elegido desde catálogo REST. |
| 2026-09-25 | `citas-web` | `npm run build` | PASS — build Vite de producción generado. |
| 2026-09-25 | `citas-api` | `mvn -Dtest=AuthIntegrationTest,SchedulingServiceIntegrationTest test` | BLOQUEADO — Maven no está instalado en el host. |
| 2026-09-25 | infraestructura | `docker version` | BLOQUEADO — Docker Desktop daemon no está iniciado o no está accesible. |

## Pendiente para declarar cierre S3

1. Iniciar Docker Desktop.
2. Ejecutar desde `citas-api`: `docker compose -f compose.test.yml run --rm api-test mvn -Dtest=AuthIntegrationTest,SchedulingServiceIntegrationTest test`.
3. Ejecutar la suite completa backend: `docker compose -f compose.test.yml run --rm api-test mvn test`.
4. Levantar el entorno desde `citas/docker-compose.yml` y recorrer registro con/sin plan, reserva general, reserva especializada, aprobación, rechazo y conflicto de doble reserva.
5. Adjuntar las salidas de esas ejecuciones, una demostración Red → Green y la demostración FAIL/PASS del hook de secretos antes de marcar HU de S3 como completadas.

## Regla de cierre

Una prueba presente en el repositorio no equivale a evidencia de cierre: las HU S3 permanecen en progreso hasta que las pruebas backend y la validación cross-repo con Docker tengan resultado PASS registrado.
