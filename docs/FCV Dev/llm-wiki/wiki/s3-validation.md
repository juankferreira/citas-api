# Validación S3

Fecha de actualización: 2026-09-25.

## Alcance comprobado en código

- Afiliación opcional durante el registro: catálogo de planes activos, registro sin plan, registro con `insurancePlanId`, FK en `user_insurance_affiliations` y rechazo de planes inexistentes o inactivos sin usuario parcial.
- Reserva concurrente: bloqueo transaccional de slots, una sola reserva válida y conflicto para el segundo intento; cobertura tanto para `APPROVED` (general) como `REQUESTED` (especializada). La integración también comprueba duración exclusiva 30/60, historial `USER`/`ADMIN`, motivo de rechazo obligatorio y que un bloque con slots retenidos no se elimina.
- Zona de negocio: el `Clock` de la aplicación usa `America/Bogota`.

## Ejecuciones registradas

| Fecha | Repositorio | Comando | Resultado |
|---|---|---|---|
| 2026-09-25 | `citas-web` | `npm run lint` | PASS — TypeScript sin errores. |
| 2026-09-25 | `citas-web` | `npm test` | PASS — 3 archivos y 10 pruebas Vitest. Incluye registro mínimo y registro con plan elegido desde catálogo REST. |
| 2026-09-25 | `citas-web` | `npm run build` | PASS — build Vite de producción generado. |
| 2026-09-25 | `citas-api` | `docker compose -f compose.test.yml run --rm api-test mvn -Dtest=AuthIntegrationTest,SchedulingServiceIntegrationTest test` | PASS — ejecución confirmada con `BUILD SUCCESS`. |
| 2026-09-25 | `citas-api` | `docker compose -f compose.test.yml run --rm api-test mvn test` | PASS — suite completa confirmada con `BUILD SUCCESS`. |
| 2026-09-25 | infraestructura | `docker version` | PASS — Docker Desktop 29.8.0 accesible con autorización local. |

## Pendiente para declarar cierre S3

1. Iniciar Docker Desktop.
2. Levantar el entorno desde `citas/docker-compose.yml` y recorrer registro con/sin plan, reserva general, reserva especializada, aprobación, rechazo y conflicto de doble reserva.
3. Adjuntar las salidas de esas ejecuciones, una demostración Red → Green y la demostración FAIL/PASS del hook de secretos antes de marcar HU de S3 como completadas.

## Calidad de hooks

Los hooks versionados están configurados con `core.hooksPath=.githooks` en ambos repositorios. La prueba controlada de secreto ficticio se intentó en backend: antes de configurar el path, Git permitió un commit temporal, que fue retirado localmente sin publicarlo; después de configurarlo, Git intentó ejecutar el hook pero Git Bash falló antes de evaluarlo con `couldn't create signal pipe, Win32 error 5`. Por ello la evidencia FAIL/PASS permanece pendiente y debe repetirse en una terminal Windows sin esa restricción.

## Regla de cierre

Una prueba presente en el repositorio no equivale a evidencia de cierre: las HU S3 permanecen en progreso hasta que las pruebas backend y la validación cross-repo con Docker tengan resultado PASS registrado.
