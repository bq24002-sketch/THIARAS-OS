-- ============================================================
-- THIARAS OS
-- FUNCIÓN DE PROCESAMIENTO DE CUMPLIMIENTO
-- ============================================================

CREATE OR REPLACE FUNCTION procesar_cumplimiento(
    p_cumplimiento_id BIGINT
)
RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    v_actividad_id BIGINT;
    v_estado VARCHAR(30);
BEGIN

    -- ========================================================
    -- 1. OBTENER Y VALIDAR EL CUMPLIMIENTO
    -- ========================================================

    SELECT
        actividad_id,
        estado
    INTO
        v_actividad_id,
        v_estado
    FROM cumplimiento_actividad
    WHERE id = p_cumplimiento_id;


    IF NOT FOUND THEN

        RAISE EXCEPTION
            'No existe el cumplimiento con id %',
            p_cumplimiento_id;

    END IF;


    -- ========================================================
    -- 2. VALIDAR QUE ESTÉ COMPLETADO
    -- ========================================================

    IF v_estado <> 'COMPLETADA' THEN

        RAISE EXCEPTION
            'El cumplimiento % no está COMPLETADA',
            p_cumplimiento_id;

    END IF;


    -- ========================================================
    -- 3. EVALUAR LOGROS POR ACTIVIDADES
    -- ========================================================
    -- Se consideran todos los cumplimientos completados
    -- porque esta métrica es acumulativa.
    --
    -- ON CONFLICT evita volver a registrar un logro
    -- que ya fue obtenido.
    -- ========================================================

    INSERT INTO logro_obtenido (
        logro_id,
        fecha_obtenido,
        valor_alcanzado
    )
    SELECT
        l.id,
        CURRENT_DATE,
        COUNT(ca.id)::INTEGER
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


    -- ========================================================
    -- 4. EVALUAR LOGROS POR MINUTOS
    -- ========================================================

    INSERT INTO logro_obtenido (
        logro_id,
        fecha_obtenido,
        valor_alcanzado
    )
    SELECT
        l.id,
        CURRENT_DATE,
        SUM(ca.minutos_realizados)::INTEGER
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


    -- ========================================================
    -- 5. GENERAR NOTIFICACIONES PARA NUEVOS LOGROS
    -- ========================================================

    INSERT INTO notificacion (
        tipo,
        titulo,
        mensaje,
        logro_id
    )
    SELECT
        'LOGRO',
        '🎉 ' || l.nombre,
        '¡Felicidades! Has desbloqueado el logro "' ||
            l.nombre ||
            '".',
        lo.logro_id
    FROM logro_obtenido lo
    JOIN logro l
        ON l.id = lo.logro_id
    WHERE NOT EXISTS (
        SELECT 1
        FROM notificacion n
        WHERE n.tipo = 'LOGRO'
          AND n.logro_id = lo.logro_id
    );


END;
$$;