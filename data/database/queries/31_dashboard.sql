-- ============================================================
-- THIARAS OS
-- DASHBOARD PRINCIPAL
-- ============================================================

-- ============================================================
-- 1. RESUMEN DE ACTIVIDADES Y MINUTOS
-- ============================================================

SELECT
    COUNT(*) FILTER (
        WHERE fecha = CURRENT_DATE
          AND estado = 'COMPLETADA'
    ) AS actividades_hoy,

    COALESCE(
        SUM(minutos_realizados) FILTER (
            WHERE fecha = CURRENT_DATE
              AND estado = 'COMPLETADA'
        ),
        0
    ) AS minutos_hoy,

    COUNT(*) FILTER (
        WHERE fecha >= DATE_TRUNC(
            'week',
            CURRENT_DATE
        )::DATE
        AND fecha < (
            DATE_TRUNC(
                'week',
                CURRENT_DATE
            ) + INTERVAL '7 days'
        )::DATE
        AND estado = 'COMPLETADA'
    ) AS actividades_semana,

    COALESCE(
        SUM(minutos_realizados) FILTER (
            WHERE fecha >= DATE_TRUNC(
                'week',
                CURRENT_DATE
            )::DATE
            AND fecha < (
                DATE_TRUNC(
                    'week',
                    CURRENT_DATE
                ) + INTERVAL '7 days'
            )::DATE
            AND estado = 'COMPLETADA'
        ),
        0
    ) AS minutos_semana

FROM cumplimiento_actividad;


-- ============================================================
-- 2. OBJETIVOS ACTIVOS
-- ============================================================

SELECT
    o.id,
    o.nombre,
    o.metrica,
    o.meta,
    o.periodo,
    o.fecha_inicio,
    o.fecha_fin,

    CASE
        WHEN o.metrica = 'ACTIVIDADES' THEN
            (
                SELECT COUNT(*)
                FROM cumplimiento_actividad ca
                WHERE ca.estado = 'COMPLETADA'
                  AND ca.fecha >= o.fecha_inicio
                  AND (
                        o.fecha_fin IS NULL
                        OR ca.fecha <= o.fecha_fin
                      )
            )::INTEGER

        WHEN o.metrica = 'MINUTOS' THEN
            (
                SELECT COALESCE(
                    SUM(ca.minutos_realizados),
                    0
                )
                FROM cumplimiento_actividad ca
                WHERE ca.estado = 'COMPLETADA'
                  AND ca.fecha >= o.fecha_inicio
                  AND (
                        o.fecha_fin IS NULL
                        OR ca.fecha <= o.fecha_fin
                      )
            )::INTEGER
    END AS realizado,

    ROUND(
        LEAST(
            100,
            (
                CASE
                    WHEN o.metrica = 'ACTIVIDADES' THEN
                        (
                            SELECT COUNT(*)
                            FROM cumplimiento_actividad ca
                            WHERE ca.estado = 'COMPLETADA'
                              AND ca.fecha >= o.fecha_inicio
                              AND (
                                    o.fecha_fin IS NULL
                                    OR ca.fecha <= o.fecha_fin
                                  )
                        )::NUMERIC

                    WHEN o.metrica = 'MINUTOS' THEN
                        (
                            SELECT COALESCE(
                                SUM(ca.minutos_realizados),
                                0
                            )
                            FROM cumplimiento_actividad ca
                            WHERE ca.estado = 'COMPLETADA'
                              AND ca.fecha >= o.fecha_inicio
                              AND (
                                    o.fecha_fin IS NULL
                                    OR ca.fecha <= o.fecha_fin
                                  )
                        )::NUMERIC
                END
            ) * 100 / o.meta
        ),
        2
    ) AS porcentaje

FROM objetivo o

WHERE o.activo = TRUE
  AND o.fecha_inicio <= CURRENT_DATE
  AND (
        o.fecha_fin IS NULL
        OR o.fecha_fin >= CURRENT_DATE
      )

ORDER BY
    o.id;


-- ============================================================
-- 3. LOGROS
-- ============================================================

SELECT
    l.id,
    l.codigo,
    l.nombre,
    l.metrica,
    l.meta,

    CASE
        WHEN lo.id IS NOT NULL
        THEN TRUE
        ELSE FALSE
    END AS desbloqueado,

    lo.fecha_obtenido,
    lo.valor_alcanzado

FROM logro l

LEFT JOIN logro_obtenido lo
    ON lo.logro_id = l.id

WHERE l.activo = TRUE

ORDER BY
    l.id;


-- ============================================================
-- 4. NOTIFICACIONES PENDIENTES
-- ============================================================

SELECT
    n.id,
    n.tipo,
    n.titulo,
    n.mensaje,
    n.actividad_id,
    n.logro_id,
    n.creada_en

FROM notificacion n

WHERE n.leida = FALSE

ORDER BY
    n.creada_en DESC,
    n.id DESC;