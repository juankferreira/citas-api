# Entrega S4 — ciclo de vida de citas

## Contrato implementado

| Actor | Método y ruta | Resultado |
| --- | --- | --- |
| USER | `GET /api/v1/appointments/mine?status&date` | Lista propia filtrable. |
| USER | `POST /api/v1/appointments/{id}/cancel` | Cancela futura no terminal, libera slots y registra historial. |
| USER | `POST /api/v1/appointments/{id}/reschedule` | Retiene nueva franja para cita propia `APPROVED` futura. |
| ADMIN | `GET /api/v1/admin/reschedules/pending` | Bandeja de reprogramaciones pendientes. |
| ADMIN | `POST /api/v1/admin/reschedules/{id}/decision` | Aprueba intercambio de franjas o rechaza/libera retención. |
| PROFESSIONAL | `GET /api/v1/professional/appointments` | Agenda propia de citas aprobadas. |
| PROFESSIONAL | `POST /api/v1/professional/appointments/{id}/close` | Cierra cita propia cuyo fin ya pasó como `COMPLETED` o `NO_SHOW`. |
| USER/PROFESSIONAL/ADMIN | `GET /api/v1/appointments/{id}/history` | Historial protegido por ownership o rol ADMIN. |

## Integridad

`reschedule_requests` conserva la cita original y sus slots; `professional_slots.reschedule_request_id` retiene solo la nueva franja. La aprobación libera los slots antiguos y convierte la retención en asignación de la misma cita dentro de una transacción. El rechazo y la cancelación liberan la retención.

## Evidencia local 2026-09-30

- Backend: `mvn test` — PASS, 16 pruebas, incluyendo integración REST/persistencia con MySQL 8.4 en Testcontainers y Flyway V1–V3.
- Frontend: `npm run lint`, `npm test`, `npm run build` — PASS; 14 pruebas en Vitest.
