-- ============================================================
-- THIARAS OS
-- RESUMEN GENERAL DE PROGRESO
-- ============================================================

SELECT
    -- ========================================================
    -- HOY
    -- ========================================================

    COUNT(*) FILTER (
        WHERE fecha = CURRENT_DATE
          AND estado = 'COMPLETADA'
    ) AS actividades_completadas_hoy,

    COALESCE(
        SUM(minutos_realizados) FILTER (
            WHERE fecha = CURRENT_DATE
              AND estado = 'COMPLETADA'
        ),
        0
    ) AS minutos_estudiados_hoy,


    -- ========================================================
    -- SEMANA ACTUAL
    -- ========================================================

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
    ) AS actividades_completadas_semana,

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
    ) AS minutos_estudiados_semana,


    -- ========================================================
    -- HISTÓRICO
    -- ========================================================

    COUNT(*) FILTER (
        WHERE estado = 'COMPLETADA'
    ) AS actividades_completadas_total,

    COALESCE(
        SUM(minutos_realizados) FILTER (
            WHERE estado = 'COMPLETADA'
        ),
        0
    ) AS minutos_estudiados_total

FROM cumplimiento_actividad;