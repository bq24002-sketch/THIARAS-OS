-- ============================================================
-- THIARAS OS
-- COMPLETAR ACTIVIDAD
-- ============================================================

BEGIN;

-- ============================================================
-- 1. COMPLETAR LA ULTIMA EJECUCION EN PROGRESO
-- ============================================================

UPDATE cumplimiento_actividad
SET
    momento_fin = CURRENT_TIMESTAMP,

    minutos_realizados =
        GREATEST(
            1,
            FLOOR(
                EXTRACT(
                    EPOCH FROM (
                        CURRENT_TIMESTAMP - momento_inicio
                    )
                ) / 60
            )::INTEGER
        ),

    estado = 'COMPLETADA'

WHERE id = (
    SELECT id
    FROM cumplimiento_actividad
    WHERE actividad_id = 2
      AND estado = 'EN_PROGRESO'
    ORDER BY id DESC
    LIMIT 1
);

-- ============================================================
-- 2. PROCESAR EL CUMPLIMIENTO COMPLETADO
-- ============================================================

DO $$
DECLARE
    v_cumplimiento_id BIGINT;
BEGIN

    SELECT id
    INTO v_cumplimiento_id
    FROM cumplimiento_actividad
    WHERE actividad_id = 2
      AND estado = 'COMPLETADA'
    ORDER BY id DESC
    LIMIT 1;

    IF v_cumplimiento_id IS NOT NULL THEN
        PERFORM procesar_cumplimiento(v_cumplimiento_id);
    END IF;

END;
$$;

COMMIT;


-- ============================================================
-- 3. VERIFICACION
-- ============================================================

SELECT
    ca.id,
    ca.actividad_id,
    a.titulo,
    ca.momento_inicio,
    ca.momento_fin,
    ca.minutos_realizados,
    ca.estado
FROM cumplimiento_actividad ca
JOIN actividad a
    ON a.id = ca.actividad_id
WHERE ca.actividad_id = 2
ORDER BY ca.id DESC
LIMIT 1;