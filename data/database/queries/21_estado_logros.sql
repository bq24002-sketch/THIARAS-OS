-- ============================================================
-- THIARAS OS
-- ESTADO DE LOS LOGROS
-- ============================================================

SELECT
    l.id,
    l.codigo,
    l.nombre,
    l.metrica,
    l.meta,

    CASE
        WHEN l.metrica = 'ACTIVIDADES'
        THEN COUNT(ca.id)

        WHEN l.metrica = 'MINUTOS'
        THEN COALESCE(
            SUM(ca.minutos_realizados),
            0
        )

        ELSE 0
    END AS progreso,

    CASE
        WHEN l.metrica = 'ACTIVIDADES'
        THEN GREATEST(
            l.meta - COUNT(ca.id),
            0
        )

        WHEN l.metrica = 'MINUTOS'
        THEN GREATEST(
            l.meta -
            COALESCE(
                SUM(ca.minutos_realizados),
                0
            ),
            0
        )

        ELSE l.meta
    END AS restante,

    ROUND(
        LEAST(
            100.0,
            (
                CASE
                    WHEN l.metrica = 'ACTIVIDADES'
                    THEN COUNT(ca.id)

                    WHEN l.metrica = 'MINUTOS'
                    THEN COALESCE(
                        SUM(ca.minutos_realizados),
                        0
                    )

                    ELSE 0
                END
                * 100.0
                / l.meta
            )
        ),
        2
    ) AS porcentaje,

    CASE
        WHEN lo.id IS NOT NULL
        THEN 'OBTENIDO'

        ELSE 'PENDIENTE'
    END AS estado

FROM logro l

LEFT JOIN cumplimiento_actividad ca
    ON ca.estado = 'COMPLETADA'

LEFT JOIN logro_obtenido lo
    ON lo.logro_id = l.id

WHERE l.activo = TRUE

GROUP BY
    l.id,
    l.codigo,
    l.nombre,
    l.metrica,
    l.meta,
    lo.id

ORDER BY
    l.id;