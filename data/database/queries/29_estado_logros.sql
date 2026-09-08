-- ============================================================
-- THIARAS OS
-- ESTADO DE LOGROS
-- ============================================================

SELECT
    l.id,
    l.codigo,
    l.nombre,
    l.descripcion,
    l.metrica,
    l.meta,
    l.activo,

    -- ========================================================
    -- PROGRESO ACTUAL
    -- ========================================================

    CASE
        WHEN l.metrica = 'ACTIVIDADES' THEN
            (
                SELECT COUNT(*)
                FROM cumplimiento_actividad ca
                WHERE ca.estado = 'COMPLETADA'
            )::INTEGER

        WHEN l.metrica = 'MINUTOS' THEN
            (
                SELECT COALESCE(
                    SUM(ca.minutos_realizados),
                    0
                )
                FROM cumplimiento_actividad ca
                WHERE ca.estado = 'COMPLETADA'
            )::INTEGER
    END AS progreso_actual,


    -- ========================================================
    -- PORCENTAJE
    -- ========================================================

    ROUND(
        LEAST(
            100,
            (
                CASE
                    WHEN l.metrica = 'ACTIVIDADES' THEN
                        (
                            SELECT COUNT(*)
                            FROM cumplimiento_actividad ca
                            WHERE ca.estado = 'COMPLETADA'
                        )::NUMERIC

                    WHEN l.metrica = 'MINUTOS' THEN
                        (
                            SELECT COALESCE(
                                SUM(ca.minutos_realizados),
                                0
                            )
                            FROM cumplimiento_actividad ca
                            WHERE ca.estado = 'COMPLETADA'
                        )::NUMERIC
                END
            ) * 100 / l.meta
        ),
        2
    ) AS porcentaje,


    -- ========================================================
    -- ESTADO
    -- ========================================================

    CASE
        WHEN lo.id IS NOT NULL THEN
            'DESBLOQUEADO'

        ELSE
            'PENDIENTE'
    END AS estado,


    -- ========================================================
    -- INFORMACIÓN DEL LOGRO OBTENIDO
    -- ========================================================

    lo.fecha_obtenido,
    lo.momento_obtenido,
    lo.valor_alcanzado

FROM logro l

LEFT JOIN logro_obtenido lo
    ON lo.logro_id = l.id

WHERE l.activo = TRUE

ORDER BY
    l.id;