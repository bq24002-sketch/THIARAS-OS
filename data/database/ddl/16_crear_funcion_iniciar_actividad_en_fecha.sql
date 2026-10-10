CREATE OR REPLACE FUNCTION iniciar_actividad_en_fecha(
    p_actividad_id BIGINT,
    p_fecha DATE
)
RETURNS BIGINT
LANGUAGE plpgsql
AS $function$
DECLARE
    v_cumplimiento_id BIGINT;
    v_recurrente BOOLEAN;
    v_fecha_inicio DATE;
    v_fecha_vencimiento DATE;
    v_tipo_recurrencia VARCHAR(30);
    v_intervalo INTEGER;
    v_dia_semana INTEGER;
    v_fecha_fin DATE;
BEGIN

    -- ========================================================
    -- 1. VALIDAR ACTIVIDAD
    -- ========================================================

    SELECT
    a.recurrente,
    a.fecha_inicio,
    a.fecha_vencimiento,
    r.tipo,
    r.intervalo,
    r.dia_semana,
    r.fecha_fin
INTO
    v_recurrente,
    v_fecha_inicio,
    v_fecha_vencimiento,
    v_tipo_recurrencia,
    v_intervalo,
    v_dia_semana,
    v_fecha_fin
FROM actividad a
LEFT JOIN LATERAL (
    SELECT
        r.tipo,
        r.intervalo,
        r.dia_semana,
        r.fecha_fin
    FROM recurrencia r
    WHERE r.actividad_id = a.id
      AND (
          r.dia_semana IS NULL
          OR r.dia_semana =
              EXTRACT(ISODOW FROM p_fecha)::INTEGER
      )
      AND (
          r.fecha_fin IS NULL
          OR p_fecha <= r.fecha_fin
      )
    ORDER BY r.id
    LIMIT 1
) r ON TRUE
WHERE a.id = p_actividad_id
  AND a.activa = TRUE;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'La actividad % no existe o esta inactiva.',
            p_actividad_id;
    END IF;


    -- ========================================================
    -- 2. VALIDAR FECHA DE OCURRENCIA
    -- ========================================================

    IF p_fecha IS NULL THEN
        RAISE EXCEPTION
            'La fecha de la ocurrencia no puede ser NULL.';
    END IF;


    -- ========================================================
    -- ACTIVIDAD NO RECURRENTE
    -- Solo puede ejecutarse en su fecha exacta.
    -- ========================================================

    IF v_recurrente = FALSE THEN

        IF p_fecha <> v_fecha_inicio THEN
            RAISE EXCEPTION
                'La actividad % no tiene una ocurrencia válida para %.',
                p_actividad_id,
                p_fecha;
        END IF;

    ELSE

        -- ====================================================
        -- ACTIVIDAD RECURRENTE
        -- ====================================================
    IF v_tipo_recurrencia IS NULL THEN
    RAISE EXCEPTION
        'La actividad % no tiene una recurrencia configurada para la fecha %.',
        p_actividad_id,
        p_fecha;
    END IF;
        IF v_tipo_recurrencia <> 'SEMANAL' THEN
            RAISE EXCEPTION
                'La actividad % tiene una recurrencia no soportada: %.',
                p_actividad_id,
                v_tipo_recurrencia;
        END IF;

        IF p_fecha < v_fecha_inicio THEN
            RAISE EXCEPTION
                'La fecha % es anterior al inicio de la actividad %.',
                p_fecha,
                p_actividad_id;
        END IF;

        IF v_fecha_vencimiento IS NOT NULL
           AND p_fecha > v_fecha_vencimiento THEN
            RAISE EXCEPTION
                'La fecha % supera el vencimiento de la actividad %.',
                p_fecha,
                p_actividad_id;
        END IF;

        IF v_fecha_fin IS NOT NULL
           AND p_fecha > v_fecha_fin THEN
            RAISE EXCEPTION
                'La fecha % supera el fin de la recurrencia de la actividad %.',
                p_fecha,
                p_actividad_id;
        END IF;

        IF v_dia_semana IS NULL
           OR v_dia_semana <> EXTRACT(ISODOW FROM p_fecha)::INTEGER THEN
            RAISE EXCEPTION
                'La fecha % no corresponde al día configurado para la actividad %.',
                p_fecha,
                p_actividad_id;
        END IF;

        IF FLOOR(
            (p_fecha - v_fecha_inicio) / 7
        )::INTEGER % v_intervalo <> 0 THEN
            RAISE EXCEPTION
                'La fecha % no corresponde al intervalo de recurrencia de la actividad %.',
                p_fecha,
                p_actividad_id;
        END IF;

    END IF;


    -- ========================================================
    -- 3. EVITAR DOS EJECUCIONES SIMULTÁNEAS
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
    -- 4. CREAR CUMPLIMIENTO
    -- ========================================================

    INSERT INTO cumplimiento_actividad (
        actividad_id,
        fecha,
        momento_inicio,
        estado
    )
    VALUES (
        p_actividad_id,
        p_fecha,
        CURRENT_TIMESTAMP,
        'EN_PROGRESO'
    )
    RETURNING id
    INTO v_cumplimiento_id;


    -- ========================================================
    -- 5. DEVOLVER ID
    -- ========================================================

    RETURN v_cumplimiento_id;

END;
$function$;