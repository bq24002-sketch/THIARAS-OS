-- ============================================================
-- THIARAS OS
-- CONSULTA - ACTIVIDADES CON VENCIMIENTO
-- ============================================================

SELECT
    a.id,
    a.titulo,
    a.materia,
    t.nombre AS tipo,
    a.fecha_inicio,
    a.fecha_vencimiento,
    CURRENT_DATE - a.fecha_inicio
        AS dias_transcurridos,

    a.fecha_vencimiento - CURRENT_DATE
        AS dias_restantes

FROM actividad a

JOIN tipo_actividad t
    ON t.id = a.tipo_id

WHERE
    a.activa = TRUE
    AND a.fecha_vencimiento IS NOT NULL

ORDER BY
    a.fecha_vencimiento;