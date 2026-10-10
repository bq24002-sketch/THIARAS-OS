-- ============================================================
-- THIARAS OS
-- ACTIVIDADES PARA LA INTERFAZ - AGENDA DEL DÍA
-- ============================================================

SELECT
    a.id,
    a.titulo,
    a.materia,
    a.contenido,
    a.duracion_minutos,
    a.fecha_inicio,
    a.fecha_vencimiento,
    a.hora,
    a.recurrente,
    a.activa,

    COALESCE(
        ca.estado,
        'PENDIENTE'
    ) AS estado,

    ca.id AS cumplimiento_id,
    ca.momento_inicio,
    ca.momento_fin,
    ca.minutos_realizados

FROM actividad a

LEFT JOIN recurrencia r
    ON r.actividad_id = a.id

LEFT JOIN LATERAL (
    SELECT
        c.id,
        c.estado,
        c.momento_inicio,
        c.momento_fin,
        c.minutos_realizados
    FROM cumplimiento_actividad c
    WHERE c.actividad_id = a.id
      AND c.fecha = CURRENT_DATE
    ORDER BY c.id DESC
    LIMIT 1
) ca
    ON TRUE

WHERE
    a.activa = TRUE

    AND (

        -- ====================================================
        -- ACTIVIDAD NO RECURRENTE
        -- ====================================================

        (
            a.recurrente = FALSE

            AND CURRENT_DATE >= a.fecha_inicio

            AND (
                a.fecha_vencimiento IS NULL
                OR CURRENT_DATE <= a.fecha_vencimiento
            )
        )

        OR

        -- ====================================================
        -- ACTIVIDAD RECURRENTE
        -- ====================================================

(
        a.recurrente = TRUE

        AND r.tipo = 'SEMANAL'

        AND r.dia_semana =
        EXTRACT(ISODOW FROM CURRENT_DATE)

        AND a.fecha_inicio <= CURRENT_DATE

        AND (
            r.fecha_fin IS NULL
            OR r.fecha_fin >= CURRENT_DATE
        )

        AND (
            FLOOR(
            (
                CURRENT_DATE - a.fecha_inicio
                ) / 7
            )::INTEGER % r.intervalo
        ) = 0
    )
)

    -- ========================================================
    -- UNA ACTIVIDAD COMPLETADA HOY YA NO APARECE EN LA AGENDA
    -- ========================================================

    AND (
        ca.estado IS NULL
        OR ca.estado <> 'COMPLETADA'
    )

ORDER BY
    a.hora NULLS LAST,
    a.id;