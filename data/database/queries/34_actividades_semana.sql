-- ============================================================
-- THIARAS OS
-- ACTIVIDADES PLANIFICADAS PARA LA SEMANA ACTUAL
-- ============================================================

WITH semana AS (
    SELECT generate_series(
        DATE_TRUNC('week', CURRENT_DATE)::date,
        (DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '6 days')::date,
        INTERVAL '1 day'
    )::date AS fecha
),
actividades_programadas AS (
    SELECT
        a.id,
        a.titulo,
        a.materia,
        s.fecha,
        a.hora,
        a.duracion_minutos,
        a.recurrente
    FROM actividad a
    CROSS JOIN semana s
    LEFT JOIN recurrencia r
        ON r.actividad_id = a.id
    WHERE a.activa = TRUE
      AND (
          -- ====================================================
          -- ACTIVIDAD NO RECURRENTE
          -- Solo existe en su fecha programada.
          -- ====================================================
          (
              a.recurrente = FALSE
              AND s.fecha = a.fecha_inicio
          )

          OR

          -- ====================================================
          -- ACTIVIDAD RECURRENTE SEMANAL
          -- Aparece únicamente en el día de la semana
          -- configurado para su recurrencia.
          -- ====================================================
          (
              a.recurrente = TRUE
            AND r.tipo = 'SEMANAL'
            AND r.dia_semana = EXTRACT(ISODOW FROM s.fecha)::integer
            AND s.fecha >= a.fecha_inicio
            AND (
                a.fecha_vencimiento IS NULL
                OR s.fecha <= a.fecha_vencimiento
                )
            AND (
                r.fecha_fin IS NULL
                OR s.fecha <= r.fecha_fin
            )
              AND FLOOR(
                  (s.fecha - a.fecha_inicio) / 7
              )::integer % r.intervalo = 0
          )
      )
)
SELECT
    ap.id,
    ap.titulo,
    ap.materia,
    ap.fecha,
    ap.hora,
    ap.duracion_minutos,
    ap.recurrente,
    COALESCE(ca.estado, 'PENDIENTE') AS estado
FROM actividades_programadas ap
LEFT JOIN LATERAL (
    SELECT c.estado
    FROM cumplimiento_actividad c
    WHERE c.actividad_id = ap.id
      AND c.fecha = ap.fecha
    ORDER BY c.id DESC
    LIMIT 1
) ca ON TRUE
ORDER BY
    ap.fecha,
    ap.hora NULLS LAST,
    ap.id;