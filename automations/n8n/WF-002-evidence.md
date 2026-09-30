# WF-002 — Evidencia de prueba controlada

- Fecha: 2026-09-30.
- Flujo: `Webhook` → `Datos de prueba de evento` → `Gmail: Draft/Create` → `Respond to Webhook`.
- Resultado: ejecución manual correcta y borrador de actualización de estado creado en la cuenta Gmail de laboratorio.
- Seguridad: el workflow permanece inactivo; no se publicó, no se envió correo y el JSON versionado excluye credenciales, IDs de instancia y destinatario real.
- Paso de producción pendiente: `citas-api` debe invocar el webhook por HTTPS con autenticación y payload validado antes de activar el flujo.
