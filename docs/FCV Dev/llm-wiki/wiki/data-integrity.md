# Datos e integridad

## HECHO

El diseño debe alcanzar 3FN, resolver relaciones N:M con tablas puente, evitar duplicación de catálogos y justificar claves, índices, snapshots, auditoría y prevención de doble reserva.

## DECISIÓN — 2026-09-25 · Integridad del corte S3

La agenda usa la zona de negocio `America/Bogota`. Los contratos reciben fecha `YYYY-MM-DD` y hora `HH:mm`; las reglas de futuro y la creación de slots se evalúan con un `Clock` configurado para esa zona.

La reserva bloquea las filas candidatas de `professional_slots` con `FOR UPDATE` dentro de la misma transacción que crea la cita y asigna los slots. Si falta un slot, los slots no son consecutivos o alguno ya tiene una cita, la operación termina con conflicto y no se crea una cita parcial. Las solicitudes especializadas `REQUESTED` retienen los slots; una decisión ADMIN `REJECTED` los libera en esa misma transacción, mientras que `APPROVED` los conserva.

La afiliación inicial es opcional y está normalizada en `user_insurance_affiliations`; `users` no guarda nombres de EPS ni de plan. Al recibirse `insurancePlanId`, el plan debe existir y estar activo; de lo contrario el registro se revierte sin persistir un usuario parcial.

## DECISIÓN — 2026-09-22 · Modelo de identidad integrado

Flyway V1 usa el modelo 3FN de referencia: `users`, `roles`, `user_roles` y `refresh_tokens`. La relación usuario–rol es N:M mediante puente y cada refresh referencia exactamente un usuario. Email canónico tiene UK global; documento usa UK `(document_type, document_number)`. `token_hash` tiene UK y nunca se guarda el JWT. El índice de usuario y la búsqueda por hash con bloqueo de fila permiten rotación concurrente.

Los atributos personales dependen solo de `users.id`, nombres de rol solo de `roles.id` y vigencia/revocación solo de `refresh_tokens.id`; no hay listas ni dependencias parciales o transitivas entre atributos no clave en este corte. Flyway registra baseline 0 para adoptar una base de referencia ya cargada y ejecuta V1 idempotente.
