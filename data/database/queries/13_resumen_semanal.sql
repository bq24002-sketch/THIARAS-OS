-- ============================================================
-- THIARAS OS
-- RESUMEN SEMANAL DE ACTIVIDADES
-- ============================================================


-- ============================================================
-- 1. RESUMEN GENERAL DE LA SEMANA
-- ============================================================

SELECT
    COUNT(*) AS actividades_completadas,

    COALESCE(
        SUM(minutos_realizados),
        0
    ) AS minutos_realizados,

    COALESCE(
        ROUND(
            SUM(minutos_realizados) / 60.0,
            2
        ),
        0
    ) AS horas_realizadas

FROM cumplimiento_actividad

WHERE estado = 'COMPLETADA'

  AND fecha >= DATE_TRUNC(
        'week',
        CURRENT_DATE
      )::date

  AND fecha < (
        DATE_TRUNC(
            'week',
            CURRENT_DATE
        ) + INTERVAL '7 days'
      )::date;


-- ============================================================
-- 2. ACTIVIDADES COMPLETADAS POR TIPO
-- ============================================================

SELECT
    ta.nombre AS tipo,

    COUNT(*) AS actividades_completadas,

    COALESCE(
        SUM(ca.minutos_realizados),
        0
    ) AS minutos_realizados

FROM cumplimiento_actividad ca

JOIN actividad a
    ON a.id = ca.actividad_id

JOIN tipo_actividad ta
    ON ta.id = a.tipo_id

WHERE ca.estado = 'COMPLETADA'

  AND ca.fecha >= DATE_TRUNC(
        'week',
        CURRENT_DATE
      )::date

  AND ca.fecha < (
        DATE_TRUNC(
            'week',
            CURRENT_DATE
        ) + INTERVAL '7 days'
      )::date

GROUP BY
    ta.id,
    ta.nombre

ORDER BY
    minutos_realizados DESC;


-- ============================================================
-- 3. MINUTOS REALIZADOS POR DÍA
-- ============================================================

SELECT
    fecha,

    COUNT(*) AS actividades_completadas,

    COALESCE(
        SUM(minutos_realizados),
        0
    ) AS minutos_realizados

FROM cumplimiento_actividad

WHERE estado = 'COMPLETADA'

  AND fecha >= DATE_TRUNC(
        'week',
        CURRENT_DATE
      )::date

  AND fecha < (
        DATE_TRUNC(
            'week',
            CURRENT_DATE
        ) + INTERVAL '7 days'
      )::date

GROUP BY
    fecha

ORDER BY
    fecha;