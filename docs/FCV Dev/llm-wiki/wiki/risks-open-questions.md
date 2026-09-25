# Riesgos y preguntas abiertas

- Ambos repositorios tienen la rama local `develop`; los cambios sin commit deben revisarse y conservarse antes de cualquier integración a `main`.
- El conjunto completo de estados y transiciones posteriores a S3 (cancelación, reprogramación, cierre profesional) sigue pendiente de S4.
- La exclusión concurrente de slots y la retención de `REQUESTED` están implementadas para S3; falta ejecutar y archivar la evidencia de la prueba concurrente en un entorno con Maven y Docker disponibles.
- La zona de negocio y formato temporal de S3 están definidos como `America/Bogota`, `YYYY-MM-DD` y `HH:mm`; falta validación cross-repo con Docker.
- Es ambiguo si reservas `REQUESTED` o reprogramaciones `PENDING` bloquean la edición de bloques.
- Falta lista completa de catálogos fijos y semillas.
- La afiliación inicial opcional de S3 está definida; la gestión histórica y el CRUD ADMIN de EPS/planes pertenecen a S4.
- Los refresh tokens de HU-007 ya tienen rotación, expiración, revocación y almacenamiento definidos; queda pendiente la política futura para gestión multidispositivo.
- El contrato REST de identidad y S3 está aprobado; los contratos de S4 y posteriores siguen pendientes.
- React 19 + TypeScript + Vite es el framework frontend detectado; falta evidencia de aprobación visual y cierre cross-repo de HU-033.
- n8n necesita contrato de eventos, idempotencia, reintentos y autenticación de webhook.
- La Skill `scrum-spec-orchestrator` conserva una allowlist interna para `docs/wiki/scrum/`; debe actualizarse explícitamente antes de usarla con `docs/FCV Dev/scrum/`.
