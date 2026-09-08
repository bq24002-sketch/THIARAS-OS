-- ============================================================
-- THIARAS OS
-- CONSULTA - RECORDATORIOS
-- ============================================================

SELECT
    a.titulo,
    t.nombre AS tipo,
    a.fecha_vencimiento,
    a.hora,
    r.minutos_anticipacion,
    r.activo

FROM actividad a

JOIN tipo_actividad t
    ON t.id = a.tipo_id

JOIN recordatorio r
    ON r.actividad_id = a.id

WHERE
    a.activa = TRUE
    AND r.activo = TRUE

ORDER BY
    a.hora,
    r.minutos_anticipacion DESC;