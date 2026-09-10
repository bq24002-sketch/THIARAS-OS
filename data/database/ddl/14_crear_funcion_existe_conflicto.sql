CREATE OR REPLACE FUNCTION existe_conflicto_horario(
    p_fecha_inicio DATE,
    p_fecha_vencimiento DATE,
    p_hora TIME,
    p_duracion_minutos INTEGER,
    p_recurrente BOOLEAN,
    p_dia_semana INTEGER
)
RETURNS TABLE (
    actividad_id BIGINT,
    titulo VARCHAR,
    dia_semana INTEGER,
    inicio TIME,
    fin TIME
)
LANGUAGE SQL
AS $$
    WITH nueva AS (
        SELECT
            p_fecha_inicio AS fecha_inicio,
            p_fecha_vencimiento AS fecha_vencimiento,
            p_hora AS hora,
            p_hora + (p_duracion_minutos * INTERVAL '1 minute') AS fin,
            p_recurrente AS recurrente,
            p_dia_semana AS dia_semana
    )
    SELECT
        a.id,
        a.titulo,
        r.dia_semana,
        a.hora,
        a.hora + (a.duracion_minutos * INTERVAL '1 minute')
    FROM actividad a
    LEFT JOIN recurrencia r
        ON r.actividad_id = a.id
    CROSS JOIN nueva n
    WHERE a.activa = TRUE
      AND a.hora IS NOT NULL
      AND a.duracion_minutos IS NOT NULL

      -- Las dos actividades deben tener algún día en común.
      AND (
            (
                n.recurrente = TRUE
                AND a.recurrente = TRUE
                AND n.dia_semana = r.dia_semana
            )
            OR
            (
                n.recurrente = FALSE
                AND a.recurrente = FALSE
                AND n.fecha_inicio = a.fecha_inicio
            )
            OR
            (
                n.recurrente = FALSE
                AND a.recurrente = TRUE
                AND EXTRACT(ISODOW FROM n.fecha_inicio)::INTEGER = r.dia_semana
                AND n.fecha_inicio >= a.fecha_inicio
                AND (
                    a.fecha_vencimiento IS NULL
                    OR n.fecha_inicio <= a.fecha_vencimiento
                )
            )
            OR
            (
                n.recurrente = TRUE
                AND a.recurrente = FALSE
                AND EXTRACT(ISODOW FROM a.fecha_inicio)::INTEGER = n.dia_semana
                AND a.fecha_inicio >= n.fecha_inicio
                AND (
                    n.fecha_vencimiento IS NULL
                    OR a.fecha_inicio <= n.fecha_vencimiento
                )
            )
      )

      -- Solapamiento de intervalos horarios.
      AND n.hora < a.hora + (a.duracion_minutos * INTERVAL '1 minute')
      AND n.fin > a.hora;
$$;