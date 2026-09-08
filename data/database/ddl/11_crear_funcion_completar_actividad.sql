-- ============================================================
-- THIARAS OS
-- FUNCION PARA COMPLETAR UNA ACTIVIDAD
-- ============================================================

CREATE OR REPLACE FUNCTION completar_actividad(
    p_actividad_id BIGINT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_cumplimiento_id BIGINT;
BEGIN

    -- ========================================================
    -- 1. VALIDAR QUE LA ACTIVIDAD EXISTA
    -- ========================================================

    IF NOT EXISTS (
        SELECT 1
        FROM actividad
        WHERE id = p_actividad_id
    ) THEN

        RAISE EXCEPTION
            'La actividad % no existe.',
            p_actividad_id;

    END IF;


    -- ========================================================
    -- 2. LOCALIZAR LA ULTIMA EJECUCION EN PROGRESO
    -- ========================================================

    SELECT id
    INTO v_cumplimiento_id
    FROM cumplimiento_actividad
    WHERE actividad_id = p_actividad_id
      AND estado = 'EN_PROGRESO'
    ORDER BY id DESC
    LIMIT 1;


    -- ========================================================
    -- 3. VALIDAR QUE EXISTA UNA EJECUCION ACTIVA
    -- ========================================================

    IF v_cumplimiento_id IS NULL THEN

        RAISE EXCEPTION
            'La actividad % no tiene una ejecucion en progreso.',
            p_actividad_id;

    END IF;


    -- ========================================================
    -- 4. COMPLETAR EL CUMPLIMIENTO
    -- ========================================================

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

    WHERE id = v_cumplimiento_id;


    -- ========================================================
    -- 5. PROCESAR EL CUMPLIMIENTO
    -- ========================================================

    PERFORM procesar_cumplimiento(
        v_cumplimiento_id
    );


    -- ========================================================
    -- 6. DEVOLVER EL ID DEL CUMPLIMIENTO
    -- ========================================================

    RETURN v_cumplimiento_id;

END;
$$;