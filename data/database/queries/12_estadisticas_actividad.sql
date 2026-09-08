-- ============================================================
-- THIARAS OS
-- ESTADÍSTICAS DE ACTIVIDADES
-- ============================================================


-- ============================================================
-- 1. ACTIVIDADES COMPLETADAS
-- ============================================================

SELECT
    COUNT(*) AS actividades_completadas
FROM cumplimiento_actividad
WHERE estado = 'COMPLETADA';


-- ============================================================
-- 2. ACTIVIDADES COMPLETADAS HOY
-- ============================================================

SELECT
    COUNT(*) AS actividades_completadas_hoy
FROM cumplimiento_actividad
WHERE estado = 'COMPLETADA'
  AND fecha = CURRENT_DATE;


-- ============================================================
-- 3. MINUTOS REALIZADOS
-- ============================================================

SELECT
    COALESCE(
        SUM(minutos_realizados),
        0
    ) AS minutos_realizados
FROM cumplimiento_actividad
WHERE estado = 'COMPLETADA';


-- ============================================================
-- 4. MINUTOS REALIZADOS HOY
-- ============================================================

SELECT
    COALESCE(
        SUM(minutos_realizados),
        0
    ) AS minutos_realizados_hoy
FROM cumplimiento_actividad
WHERE estado = 'COMPLETADA'
  AND fecha = CURRENT_DATE;


-- ============================================================
-- 5. ESTADÍSTICAS POR TIPO DE ACTIVIDAD
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

GROUP BY
    ta.id,
    ta.nombre

ORDER BY
    minutos_realizados DESC;