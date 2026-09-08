-- ============================================================
-- THIARAS OS
-- ACTIVIDADES PARA LA INTERFAZ
-- ============================================================

SELECT
    a.id,
    a.titulo,
    a.materia,
    a.contenido,
    a.duracion_minutos,
    a.fecha_inicio,
    a.fecha_vencimiento,
    a.hora,
    a.recurrente,
    a.activa,

    COALESCE(
        ca.estado,
        'PENDIENTE'
    ) AS estado,

    ca.id AS cumplimiento_id,
    ca.momento_inicio,
    ca.momento_fin,
    ca.minutos_realizados

FROM actividad a

LEFT JOIN LATERAL (
    SELECT
        c.id,
        c.estado,
        c.momento_inicio,
        c.momento_fin,
        c.minutos_realizados
    FROM cumplimiento_actividad c
    WHERE c.actividad_id = a.id
    ORDER BY c.id DESC
    LIMIT 1
) ca
    ON TRUE

WHERE a.activa = TRUE

ORDER BY
    CASE
        WHEN a.fecha_vencimiento IS NULL THEN 1
        ELSE 0
    END,
    a.fecha_vencimiento,
    a.hora,
    a.id;