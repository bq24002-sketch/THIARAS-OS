-- ============================================================
-- THIARAS OS
-- OBJETIVO POR CANTIDAD DE ACTIVIDADES
-- ============================================================

BEGIN;

INSERT INTO objetivo (
    nombre,
    descripcion,
    metrica,
    meta,
    periodo,
    fecha_inicio,
    fecha_fin
)
VALUES (
    'Completar 3 actividades esta semana',
    'Objetivo semanal de actividades completadas.',
    'ACTIVIDADES',
    3,
    'SEMANAL',
    CURRENT_DATE,
    CURRENT_DATE + 6
);

COMMIT;


SELECT
    id,
    nombre,
    metrica,
    meta,
    periodo,
    fecha_inicio,
    fecha_fin,
    activo
FROM objetivo
ORDER BY id DESC
LIMIT 1;