-- ============================================================
-- THIARAS OS
-- CONSULTA - PRÓXIMAS ACTIVIDADES
-- ============================================================

SELECT
    a.id,
    a.titulo,
    a.materia,
    t.nombre AS tipo,
    a.fecha_inicio,
    a.fecha_vencimiento,
    a.hora,
    a.recurrente
FROM actividad a

JOIN tipo_actividad t
    ON t.id = a.tipo_id

WHERE
    a.activa = TRUE

    AND

    (
        a.fecha_vencimiento IS NULL
        OR
        a.fecha_vencimiento >= CURRENT_DATE
    )

ORDER BY
    CASE
        WHEN a.fecha_vencimiento IS NULL
        THEN DATE '9999-12-31'
        ELSE a.fecha_vencimiento
    END,

    a.hora;