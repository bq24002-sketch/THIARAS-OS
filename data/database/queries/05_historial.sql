-- ============================================================
-- THIARAS OS
-- CONSULTA - HISTORIAL DE EJECUCIONES
-- ============================================================

SELECT
    e.id,
    a.titulo,
    e.momento_programado,
    e.momento_ejecucion,
    e.tipo,
    e.estado,
    e.error

FROM ejecucion e

JOIN actividad a
    ON a.id = e.actividad_id

ORDER BY
    e.momento_ejecucion DESC;