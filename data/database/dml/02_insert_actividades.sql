-- ============================================================
-- THIARAS OS
-- DML - ACTIVIDADES INICIALES
-- ============================================================

INSERT INTO actividad (
    titulo,
    materia,
    contenido,
    duracion_minutos,
    tipo_id,
    fecha_inicio,
    fecha_vencimiento,
    hora,
    recurrente,
    activa
)
VALUES

(
    'Micro-sesión',
    'Programación',
    'Revisar conceptos pendientes de Java.',
    30,
    (SELECT id FROM tipo_actividad WHERE nombre = 'ESTUDIO'),
    DATE '2026-09-06',
    NULL,
    TIME '09:00',
    TRUE,
    TRUE
),

(
    'Lectura técnica',
    'Ingeniería de Software',
    'Leer documentación y tomar notas.',
    45,
    (SELECT id FROM tipo_actividad WHERE nombre = 'ESTUDIO'),
    DATE '2026-09-06',
    NULL,
    TIME '12:00',
    TRUE,
    TRUE
),

(
    'Activación Física',
    'Entrenamiento',
    'Sesión de actividad física.',
    40,
    (SELECT id FROM tipo_actividad WHERE nombre = 'EJERCICIO'),
    DATE '2026-09-06',
    NULL,
    TIME '18:30',
    TRUE,
    TRUE
),

(
    'Manejo de Estructura de Datos',
    'Estructura de Datos',
    'Sesión síncrona.',
    90,
    (SELECT id FROM tipo_actividad WHERE nombre = 'SINCRONA'),
    DATE '2026-09-06',
    NULL,
    TIME '08:20',
    TRUE,
    TRUE
),

(
    'Thiaras Demirff',
    'Escritura',
    'Ambientar capítulo.',
    60,
    (SELECT id FROM tipo_actividad WHERE nombre = 'PROYECTO'),
    DATE '2026-09-06',
    NULL,
    TIME '11:00',
    TRUE,
    TRUE
),

(
    'Desarrollo THIARAS OS',
    'Java',
    'Continuar desarrollo del sistema.',
    120,
    (SELECT id FROM tipo_actividad WHERE nombre = 'PROYECTO'),
    DATE '2026-09-06',
    NULL,
    TIME '10:00',
    TRUE,
    TRUE
),

(
    'Revisión de proyecto',
    'Ingeniería de Software',
    'Revisar avances y pendientes.',
    60,
    (SELECT id FROM tipo_actividad WHERE nombre = 'PROYECTO'),
    DATE '2026-09-06',
    NULL,
    TIME '15:00',
    TRUE,
    TRUE
),

(
    'Plan de Negocios',
    'Plan de Negocios',
    'Trabajar en el desarrollo del plan de negocios.',
    120,
    (SELECT id FROM tipo_actividad WHERE nombre = 'NEGOCIO'),
    DATE '2026-09-06',
    NULL,
    TIME '09:00',
    TRUE,
    TRUE
);