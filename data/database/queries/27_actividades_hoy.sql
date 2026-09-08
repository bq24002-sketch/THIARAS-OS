-- ============================================================
-- THIARAS OS
-- ACTIVIDADES DE HOY
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

    ca.id AS cumplimiento_id,
    ca.momento_inicio,
    ca.momento_fin,
    ca.minutos_realizados,
    ca.estado AS estado_cumplimiento

FROM actividad a

LEFT JOIN LATERAL (
    SELECT
        ca.id,
        ca.momento_inicio,
        ca.momento_fin,
        ca.minutos_realizados,
        ca.estado
    FROM cumplimiento_actividad ca
    WHERE ca.actividad_id = a.id
      AND ca.fecha = CURRENT_DATE
    ORDER BY ca.id DESC
    LIMIT 1
) ca ON TRUE

WHERE a.activa = TRUE
  AND a.fecha_inicio <= CURRENT_DATE
  AND (
        a.fecha_vencimiento IS NULL
        OR a.fecha_vencimiento >= CURRENT_DATE
      )

ORDER BY
    a.hora NULLS LAST,
    a.id;