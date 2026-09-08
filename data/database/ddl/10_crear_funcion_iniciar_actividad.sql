-- ============================================================
-- THIARAS OS
-- FUNCION PARA INICIAR UNA ACTIVIDAD
-- ============================================================

CREATE OR REPLACE FUNCTION iniciar_actividad(
    p_actividad_id BIGINT
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_cumplimiento_id BIGINT;
BEGIN

    -- ========================================================
    -- 1. VALIDAR QUE LA ACTIVIDAD EXISTA Y ESTE ACTIVA
    -- ========================================================

    IF NOT EXISTS (
        SELECT 1
        FROM actividad
        WHERE id = p_actividad_id
          AND activa = TRUE
    ) THEN

        RAISE EXCEPTION
            'La actividad % no existe o esta inactiva.',
            p_actividad_id;

    END IF;


    -- ========================================================
    -- 2. EVITAR DOS EJECUCIONES SIMULTANEAS
    -- ========================================================

    IF EXISTS (
        SELECT 1
        FROM cumplimiento_actividad
        WHERE actividad_id = p_actividad_id
          AND estado = 'EN_PROGRESO'
    ) THEN

        RAISE EXCEPTION
            'La actividad % ya esta en progreso.',
            p_actividad_id;

    END IF;


    -- ========================================================
    -- 3. CREAR EL CUMPLIMIENTO
    -- ========================================================

    INSERT INTO cumplimiento_actividad (
        actividad_id,
        fecha,
        momento_inicio,
        estado
    )
    VALUES (
        p_actividad_id,
        CURRENT_DATE,
        CURRENT_TIMESTAMP,
        'EN_PROGRESO'
    )
    RETURNING id
    INTO v_cumplimiento_id;


    -- ========================================================
    -- 4. DEVOLVER EL ID CREADO
    -- ========================================================

    RETURN v_cumplimiento_id;

END;
$$;