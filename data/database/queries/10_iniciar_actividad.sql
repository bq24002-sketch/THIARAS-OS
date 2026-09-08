-- ============================================================
-- THIARAS OS
-- INICIAR ACTIVIDAD
-- ============================================================

BEGIN;

INSERT INTO cumplimiento_actividad (
    actividad_id,
    fecha,
    momento_inicio,
    estado
)
VALUES (
    2,
    CURRENT_DATE,
    CURRENT_TIMESTAMP,
    'EN_PROGRESO'
);

COMMIT;


SELECT
    ca.id,
    ca.actividad_id,
    a.titulo,
    ca.fecha,
    ca.momento_inicio,
    ca.momento_fin,
    ca.minutos_realizados,
    ca.estado
FROM cumplimiento_actividad ca
JOIN actividad a
    ON a.id = ca.actividad_id
WHERE ca.actividad_id = 2
ORDER BY ca.id DESC
LIMIT 1;