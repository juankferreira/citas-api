# Semilla reproducible de laboratorio

Flyway aplica `V4__laboratory_demo_seed.sql` al iniciar una base nueva. Crea únicamente datos sintéticos del curso: un paciente, un profesional habilitado en HIC, un administrador, disponibilidad futura y una cita aprobada para el día siguiente.

## Cuentas de demostración

| Rol | Correo | Contraseña |
| --- | --- | --- |
| USER | `paciente.demo@lab.local` | `DemoCitas2026!` |
| PROFESSIONAL | `profesional.demo@lab.local` | `DemoCitas2026!` |
| ADMIN | `admin.demo@lab.local` | `DemoCitas2026!` |

Son credenciales públicas de laboratorio; no se deben reutilizar fuera del entorno académico.

## Revisión por otro equipo

1. Clonar los tres repositorios.
2. En `citas`, copiar `.env.example` a `.env` y ejecutar `docker compose up -d`.
3. Iniciar `citas-api` y `citas-web` según el README de cada repositorio.
4. Flyway creará esquema, catálogos y la semilla V4 automáticamente.

La cita de demostración se agenda para el día posterior a la ejecución de la migración, por lo que permanece futura al preparar un entorno nuevo.
