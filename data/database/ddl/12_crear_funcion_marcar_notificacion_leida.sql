-- ============================================================
-- THIARAS OS
-- FUNCION PARA MARCAR UNA NOTIFICACION COMO LEIDA
-- ============================================================

CREATE OR REPLACE FUNCTION marcar_notificacion_leida(
    p_notificacion_id BIGINT
)
RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN

    -- ========================================================
    -- 1. VALIDAR QUE LA NOTIFICACION EXISTA
    -- ========================================================

    IF NOT EXISTS (
        SELECT 1
        FROM notificacion
        WHERE id = p_notificacion_id
    ) THEN

        RAISE EXCEPTION
            'La notificacion % no existe.',
            p_notificacion_id;

    END IF;


    -- ========================================================
    -- 2. MARCAR COMO LEIDA
    -- ========================================================

    UPDATE notificacion
    SET
        leida = TRUE,
        leida_en = CURRENT_TIMESTAMP
    WHERE id = p_notificacion_id
      AND leida = FALSE;

END;
$$;