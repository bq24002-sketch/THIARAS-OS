-- ============================================================
-- THIARAS OS
-- LOGROS INICIALES
-- ============================================================

BEGIN;


-- ============================================================
-- ACTIVIDADES
-- ============================================================

INSERT INTO logro (
    codigo,
    nombre,
    descripcion,
    metrica,
    meta
)
VALUES
(
    'PRIMERA_ACTIVIDAD',
    'Primera actividad',
    'Completar tu primera actividad.',
    'ACTIVIDADES',
    1
),
(
    'CINCO_ACTIVIDADES',
    'Cinco actividades',
    'Completar cinco actividades.',
    'ACTIVIDADES',
    5
),
(
    'DIEZ_ACTIVIDADES',
    'Diez actividades',
    'Completar diez actividades.',
    'ACTIVIDADES',
    10
);


-- ============================================================
-- TIEMPO
-- ============================================================

INSERT INTO logro (
    codigo,
    nombre,
    descripcion,
    metrica,
    meta
)
VALUES
(
    'SESENTA_MINUTOS',
    'Primera hora',
    'Acumular 60 minutos de actividad completada.',
    'MINUTOS',
    60
),
(
    'CINCO_HORAS',
    'Cinco horas',
    'Acumular 300 minutos de actividad completada.',
    'MINUTOS',
    300
),
(
    'DIEZ_HORAS',
    'Diez horas',
    'Acumular 600 minutos de actividad completada.',
    'MINUTOS',
    600
);


COMMIT;


-- ============================================================
-- VERIFICACIÓN
-- ============================================================

SELECT
    id,
    codigo,
    nombre,
    metrica,
    meta,
    activo
FROM logro
ORDER BY id;