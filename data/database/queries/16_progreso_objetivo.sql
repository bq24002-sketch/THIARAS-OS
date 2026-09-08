-- ============================================================
-- THIARAS OS
-- PROGRESO DE OBJETIVOS
-- ============================================================

SELECT
    o.id,
    o.nombre,
    ta.nombre AS tipo,
    o.metrica,
    o.meta,

    COALESCE(
        SUM(ca.minutos_realizados),
        0
    ) AS realizado,

    GREATEST(
        o.meta - COALESCE(
            SUM(ca.minutos_realizados),
            0
        ),
        0
    ) AS restante,

    ROUND(
        LEAST(
            100.0,
            (
                COALESCE(
                    SUM(ca.minutos_realizados),
                    0
                ) * 100.0
                / o.meta
            )
        ),
        2
    ) AS porcentaje,

    CASE
        WHEN COALESCE(
            SUM(ca.minutos_realizados),
            0
        ) >= o.meta
        THEN 'COMPLETADO'

        ELSE 'EN_PROGRESO'
    END AS estado

FROM objetivo o

LEFT JOIN tipo_actividad ta
    ON ta.id = o.tipo_id

LEFT JOIN actividad a
    ON a.tipo_id = o.tipo_id

LEFT JOIN cumplimiento_actividad ca
    ON ca.actividad_id = a.id
    AND ca.estado = 'COMPLETADA'
    AND ca.fecha >= o.fecha_inicio
    AND (
        o.fecha_fin IS NULL
        OR ca.fecha <= o.fecha_fin
    )

WHERE o.activo = TRUE

GROUP BY
    o.id,
    o.nombre,
    ta.nombre,
    o.metrica,
    o.meta

ORDER BY
    o.id;