-- ============================================================
-- THIARAS OS
-- DML - RECORDATORIOS INICIALES
-- ============================================================

INSERT INTO recordatorio (
    actividad_id,
    minutos_anticipacion,
    activo
)
VALUES

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Micro-sesión'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Lectura técnica'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Activación Física'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Manejo de Estructura de Datos'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Thiaras Demirff'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Desarrollo THIARAS OS'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Revisión de proyecto'),
    0,
    TRUE
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Plan de Negocios'),
    0,
    TRUE
);