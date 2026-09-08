-- ============================================================
-- THIARAS OS
-- FUNCION PARA CONSULTAR EL ESTADO DE UNA ACTIVIDAD
-- ============================================================

CREATE OR REPLACE FUNCTION estado_actividad(
    p_actividad_id BIGINT
)
RETURNS TABLE (
    actividad_id BIGINT,
    titulo VARCHAR(150),
    duracion_minutos INTEGER,
    estado VARCHAR(30),
    cumplimiento_id BIGINT,
    momento_inicio TIMESTAMP,
    momento_fin TIMESTAMP,
    minutos_realizados INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN

    IF NOT EXISTS (
        SELECT 1
        FROM actividad
        WHERE id = p_actividad_id
    ) THEN

        RAISE EXCEPTION
            'La actividad % no existe.',
            p_actividad_id;

    END IF;


    RETURN QUERY

    SELECT
        a.id,
        a.titulo,
        a.duracion_minutos,

        COALESCE(
            ca.estado,
            'PENDIENTE'
        )::VARCHAR(30),

        ca.id,
        ca.momento_inicio,
        ca.momento_fin,
        ca.minutos_realizados

    FROM actividad a

    LEFT JOIN LATERAL (
        SELECT
            c.id,
            c.estado,
            c.momento_inicio,
            c.momento_fin,
            c.minutos_realizados
        FROM cumplimiento_actividad c
        WHERE c.actividad_id = a.id
        ORDER BY c.id DESC
        LIMIT 1
    ) ca ON TRUE

    WHERE a.id = p_actividad_id;

END;
$$;