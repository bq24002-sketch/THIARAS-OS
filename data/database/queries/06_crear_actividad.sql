-- ============================================================
-- THIARAS OS
-- CRUD - CREAR ACTIVIDAD
-- ============================================================
--
-- Laboratorio de inserción transaccional.
--
-- La interfaz Java posteriormente recopilará estos datos
-- y ejecutará una operación equivalente mediante JDBC.
-- ============================================================


BEGIN;


-- ------------------------------------------------------------
-- 1. CREAR ACTIVIDAD
-- ------------------------------------------------------------

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
VALUES (
    'Preparación examen PostgreSQL',
    'Bases de Datos',
    'Repasar DDL, DML, consultas, claves foráneas y transacciones.',
    90,

    (
        SELECT id
        FROM tipo_actividad
        WHERE nombre = 'ESTUDIO'
    ),

    DATE '2026-09-07',
    DATE '2026-09-09',
    TIME '19:00',

    FALSE,
    TRUE
);


-- ------------------------------------------------------------
-- 2. CREAR RECORDATORIOS
-- ------------------------------------------------------------

INSERT INTO recordatorio (
    actividad_id,
    minutos_anticipacion,
    activo
)
VALUES

(
    (
        SELECT id
        FROM actividad
        WHERE titulo = 'Preparación examen PostgreSQL'
    ),
    1440,
    TRUE
),

(
    (
        SELECT id
        FROM actividad
        WHERE titulo = 'Preparación examen PostgreSQL'
    ),
    60,
    TRUE
);


-- ------------------------------------------------------------
-- 3. CONFIRMAR TRANSACCIÓN
-- ------------------------------------------------------------

COMMIT;