# WF-001 — Evidencia de prueba controlada

- Fecha: 2026-09-30.
- Flujo: `Manual Trigger` → `Datos de prueba cita` → `Gmail: Draft/Create`.
- Resultado: ejecución manual correcta y borrador creado en la cuenta Gmail de laboratorio.
- Seguridad: el workflow permanece inactivo; no se publicó, no se envió correo y este repositorio excluye toda credencial, ID de instancia y destinatario real.
- Paso de producción pendiente: sustituir los datos manuales por `Schedule Trigger` + consulta HTTPS autenticada a una API pública.
