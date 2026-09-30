-- ============================================================
-- THIARAS OS
-- CONSULTA - HISTORIAL DE CUMPLIMIENTO DE ACTIVIDADES
-- ============================================================

SELECT
    ca.id,
    ca.actividad_id,
    a.titulo,
    ca.fecha,
    ca.momento_inicio,
    ca.momento_fin,
    ca.minutos_realizados,
    ca.estado,
    ca.observaciones
FROM cumplimiento_actividad ca
JOIN actividad a
    ON a.id = ca.actividad_id
ORDER BY
    ca.momento_inicio DESC;