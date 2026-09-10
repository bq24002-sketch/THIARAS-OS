SELECT EXISTS (
    SELECT 1
    FROM actividad a
    LEFT JOIN recurrencia r
        ON r.actividad_id = a.id
    WHERE a.activa = TRUE

      AND a.hora IS NOT NULL
      AND a.duracion_minutos IS NOT NULL

      AND (
            (
                ? = FALSE
                AND a.recurrente = FALSE
                AND ? BETWEEN a.fecha_inicio
                    AND COALESCE(a.fecha_vencimiento, a.fecha_inicio)
            )

            OR

            (
                ? = TRUE
                AND a.recurrente = FALSE
                AND ? <= COALESCE(a.fecha_vencimiento, a.fecha_inicio)
                AND COALESCE(?, ?) >= a.fecha_inicio
                AND EXTRACT(ISODOW FROM a.fecha_inicio)
                    = EXTRACT(ISODOW FROM ?)
            )

            OR

            (
                ? = FALSE
                AND a.recurrente = TRUE
                AND r.dia_semana = EXTRACT(ISODOW FROM ?)
                AND a.fecha_inicio <= ?
                AND (
                    r.fecha_fin IS NULL
                    OR r.fecha_fin >= ?
                )
            )

            OR

            (
                ? = TRUE
                AND a.recurrente = TRUE
                AND r.dia_semana = EXTRACT(ISODOW FROM ?)
                AND (
                    r.fecha_fin IS NULL
                    OR r.fecha_fin >= ?
                )
                AND a.fecha_inicio <= COALESCE(?, ?)
            )
      )

      AND (
          ? < a.hora + (a.duracion_minutos * INTERVAL '1 minute')
          AND a.hora < ? + (? * INTERVAL '1 minute')
      )
) AS hay_conflicto;
