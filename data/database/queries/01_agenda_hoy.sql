-- ============================================================
-- THIARAS OS
-- CONSULTA - AGENDA DEL DÍA
-- ============================================================

SELECT
    a.id,
    a.titulo,
    a.materia,
    t.nombre AS tipo,
    a.hora,
    a.duracion_minutos,
    a.recurrente,
    a.fecha_vencimiento
FROM actividad a

JOIN tipo_actividad t
    ON t.id = a.tipo_id

LEFT JOIN recurrencia r
    ON r.actividad_id = a.id

WHERE
    a.activa = TRUE

    AND
    (
        (
            a.recurrente = TRUE
            AND r.dia_semana =
                EXTRACT(
                    ISODOW FROM CURRENT_DATE
                )
        )

        OR

        (
            a.recurrente = FALSE
            AND CURRENT_DATE BETWEEN
                a.fecha_inicio
                AND COALESCE(
                    a.fecha_vencimiento,
                    a.fecha_inicio
                )
        )
    )

ORDER BY
    a.hora;