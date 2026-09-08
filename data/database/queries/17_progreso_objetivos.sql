-- ============================================================
-- THIARAS OS
-- PROGRESO DE OBJETIVOS
-- MINUTOS / ACTIVIDADES
-- ============================================================

SELECT
    o.id,
    o.nombre,
    ta.nombre AS tipo,
    o.metrica,
    o.meta,

    CASE
        WHEN o.metrica = 'MINUTOS'
        THEN COALESCE(
            SUM(ca.minutos_realizados),
            0
        )

        WHEN o.metrica = 'ACTIVIDADES'
        THEN COUNT(ca.id)

        ELSE 0
    END AS realizado,

    GREATEST(
        o.meta -
        CASE
            WHEN o.metrica = 'MINUTOS'
            THEN COALESCE(
                SUM(ca.minutos_realizados),
                0
            )

            WHEN o.metrica = 'ACTIVIDADES'
            THEN COUNT(ca.id)

            ELSE 0
        END,
        0
    ) AS restante,

    ROUND(
        LEAST(
            100.0,
            (
                CASE
                    WHEN o.metrica = 'MINUTOS'
                    THEN COALESCE(
                        SUM(ca.minutos_realizados),
                        0
                    )

                    WHEN o.metrica = 'ACTIVIDADES'
                    THEN COUNT(ca.id)

                    ELSE 0
                END
                * 100.0
                / o.meta
            )
        ),
        2
    ) AS porcentaje,

    CASE
        WHEN
            CASE
                WHEN o.metrica = 'MINUTOS'
                THEN COALESCE(
                    SUM(ca.minutos_realizados),
                    0
                )

                WHEN o.metrica = 'ACTIVIDADES'
                THEN COUNT(ca.id)

                ELSE 0
            END >= o.meta

        THEN 'COMPLETADO'

        ELSE 'EN_PROGRESO'
    END AS estado

FROM objetivo o

LEFT JOIN tipo_actividad ta
    ON ta.id = o.tipo_id

LEFT JOIN actividad a
    ON (
        o.tipo_id IS NULL
        OR a.tipo_id = o.tipo_id
    )

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