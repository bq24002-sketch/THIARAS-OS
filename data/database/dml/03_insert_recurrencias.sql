-- ============================================================
-- THIARAS OS
-- DML - RECURRENCIAS
-- ============================================================

INSERT INTO recurrencia (
    actividad_id,
    tipo,
    intervalo,
    dia_semana,
    fecha_fin
)
VALUES

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Micro-sesión'),

    'SEMANAL',
    1,
    7,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Lectura técnica'),

    'SEMANAL',
    1,
    7,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Activación Física'),

    'SEMANAL',
    1,
    7,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Manejo de Estructura de Datos'),

    'SEMANAL',
    1,
    1,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Thiaras Demirff'),

    'SEMANAL',
    1,
    1,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Desarrollo THIARAS OS'),

    'SEMANAL',
    1,
    2,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Revisión de proyecto'),

    'SEMANAL',
    1,
    3,
    NULL
),

(
    (SELECT id
     FROM actividad
     WHERE titulo = 'Plan de Negocios'),

    'SEMANAL',
    1,
    6,
    NULL
);