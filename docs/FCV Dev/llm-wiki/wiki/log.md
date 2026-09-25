# Log de operaciones

| Fecha | Operación | Fuentes/páginas | Resultado |
|---|---|---|---|
| 2026-09-17 | INGEST inicial | SRC-PRD-001, SRC-TECH-001, SRC-DB-001, SRC-TRACE-001 | Estructura y páginas iniciales creadas tras aprobación del usuario |
| 2026-09-17 | DECISIÓN | HU-001 a HU-007; `contracts.md`, `decisions.md` | Contrato y corte backend de identidad aprobados; HU-033 conserva integración web |
| 2026-09-17 | LEARN/LINT | Flyway V1, pruebas MySQL 8.4; `data-integrity.md`, `traceability.md` | Corte 3FN y evidencia backend añadidos; RAW intacto, sin nuevos enlaces estructurales |
| 2026-09-22 | LEARN/DECISIÓN | `docs/FCV Dev/subagents/`, orquestador, `index.md`, `subagents.md`, arquitectura, decisiones, riesgos y trazabilidad | Ocho subagentes versionados; protocolo de delegación y nueva ubicación documental registrados; frontend React/Vite reconocido como trabajo pendiente de verificación |
| 2026-09-22 | LINT | Catálogo de subagentes y LLM Wiki | Enlaces Markdown relativos verificados; referencias operativas del orquestador actualizadas; se conserva como riesgo explícito la allowlist histórica de la Skill Scrum |
| 2026-09-22 | LEARN/DECISIÓN | HU-033, `contracts.md`, `traceability.md` | Corte React de autenticación integrado y validado contra API/MySQL; CORS explícito y compatibilidad Flyway con el esquema 3FN existente documentados; HU-033 queda en progreso. |
| 2026-09-22 | LINT | `data-integrity.md`, HU-005 a HU-007 | Evidencia actualizada a 9/9 pruebas Maven y modelo persistente `refresh_tokens`; integración web USER confirmada sin cerrar HU posteriores. |
| 2026-09-22 | DECISIÓN | HU-001, HU-002, HU-011, HU-014 a HU-024 y `contracts.md` | Cierre S2 confirmado para fundación y modelo 3FN; corte S3 aprobado con afiliación opcional, agenda real y contrato REST compartido. |
| 2026-09-25 | LEARN/LINT | `ApplicationConfig`, `AuthIntegrationTest`, `SchedulingServiceIntegrationTest`, Wiki S3 | La zona de negocio se configuró como `America/Bogota` y se reconciliaron las decisiones de afiliación y concurrencia con el código. Las pruebas existen, pero su ejecución queda pendiente de un entorno con Maven/Docker; no se declara cierre de HU S3. |
| 2026-09-25 | VALIDACIÓN | `citas-web`: lint, Vitest y build | PASS: typecheck, 10 pruebas Vitest y build Vite de producción. La validación backend/Testcontainers y Docker queda bloqueada por herramientas no disponibles; el detalle se registra en `s3-validation.md`. |
| 2026-09-25 | VALIDACIÓN | `citas-api`: pruebas focalizadas y suite Maven mediante Docker | PASS: el usuario confirmó `BUILD SUCCESS` para `AuthIntegrationTest` + `SchedulingServiceIntegrationTest` y para la suite backend completa. Sigue pendiente el recorrido manual cross-repo y las evidencias Red → Green y hook FAIL/PASS. |
| 2026-09-25 | CALIDAD | hooks backend y frontend | `core.hooksPath=.githooks` configurado en ambos repositorios. La ejecución controlada del hook backend quedó bloqueada antes del script por un permiso Git Bash (`Win32 error 5`); no se declara evidencia FAIL/PASS. |
| 2026-09-25 | CORRECCIÓN | contrato REST de bloques y bandeja ADMIN S3 | Se alineó el cliente profesional con los DTO reales de bloques y se clarificaron los campos de solicitudes especializadas pendientes para la interfaz ADMIN. |
