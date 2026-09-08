-- ============================================================
-- THIARAS OS
-- EVALUACIÓN Y REGISTRO DE LOGROS
-- ============================================================

BEGIN;


-- ============================================================
-- 1. ACTIVIDADES COMPLETADAS
-- ============================================================

INSERT INTO logro_obtenido (
    logro_id,
    fecha_obtenido,
    valor_alcanzado
)
SELECT
    l.id,
    CURRENT_DATE,
    COUNT(ca.id)::integer

FROM logro l

JOIN cumplimiento_actividad ca
    ON ca.estado = 'COMPLETADA'

WHERE l.metrica = 'ACTIVIDADES'

  AND l.activo = TRUE

GROUP BY
    l.id,
    l.meta

HAVING COUNT(ca.id) >= l.meta

ON CONFLICT (logro_id)
DO NOTHING;


-- ============================================================
-- 2. MINUTOS ACUMULADOS
-- ============================================================

INSERT INTO logro_obtenido (
    logro_id,
    fecha_obtenido,
    valor_alcanzado
)
SELECT
    l.id,
    CURRENT_DATE,
    SUM(ca.minutos_realizados)::integer

FROM logro l

JOIN cumplimiento_actividad ca
    ON ca.estado = 'COMPLETADA'

WHERE l.metrica = 'MINUTOS'

  AND l.activo = TRUE

GROUP BY
    l.id,
    l.meta

HAVING COALESCE(
    SUM(ca.minutos_realizados),
    0
) >= l.meta

ON CONFLICT (logro_id)
DO NOTHING;


COMMIT;


-- ============================================================
-- 3. MOSTRAR LOGROS OBTENIDOS
-- ============================================================

SELECT
    lo.id,
    l.codigo,
    l.nombre,
    l.descripcion,
    lo.fecha_obtenido,
    lo.momento_obtenido,
    lo.valor_alcanzado

FROM logro_obtenido lo

JOIN logro l
    ON l.id = lo.logro_id

ORDER BY
    lo.id;