CREATE OR REPLACE FUNCTION existe_conflicto_horario(
    p_fecha_inicio DATE,
    p_fecha_vencimiento DATE,
    p_hora TIME,
    p_duracion_minutos INTEGER,
    p_recurrente BOOLEAN,
    p_dia_semana INTEGER,
    p_excluir_actividad_id BIGINT DEFAULT NULL
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
        COALESCE(
            r.dia_semana,
            EXTRACT(ISODOW FROM a.fecha_inicio)::INTEGER
        ),
        a.hora,
        a.hora + (a.duracion_minutos * INTERVAL '1 minute')
    FROM actividad a
    LEFT JOIN recurrencia r
        ON r.actividad_id = a.id
    CROSS JOIN nueva n
    WHERE a.activa = TRUE
      AND a.hora IS NOT NULL
      AND a.duracion_minutos IS NOT NULL

      -- Al editar, no comparar la actividad consigo misma.
      AND (
          p_excluir_actividad_id IS NULL
          OR a.id <> p_excluir_actividad_id
      )

      -- =====================================================
      -- COINCIDENCIA DE DÍAS Y VIGENCIA
      -- =====================================================
      AND (
          -- Nueva recurrente / existente recurrente
          (
              n.recurrente = TRUE
              AND a.recurrente = TRUE
              AND n.dia_semana = r.dia_semana
              AND a.fecha_inicio
                    <= COALESCE(
                        n.fecha_vencimiento,
                        n.fecha_inicio
                    )
              AND COALESCE(
                    a.fecha_vencimiento,
                    DATE '9999-12-31'
                  )
                    >= n.fecha_inicio
          )

          OR

          -- Nueva no recurrente / existente no recurrente
          (
              n.recurrente = FALSE
              AND a.recurrente = FALSE
              AND n.fecha_inicio = a.fecha_inicio
          )

          OR

          -- Nueva no recurrente / existente recurrente
          (
              n.recurrente = FALSE
              AND a.recurrente = TRUE
              AND EXTRACT(
                    ISODOW FROM n.fecha_inicio
                  )::INTEGER = r.dia_semana
              AND n.fecha_inicio >= a.fecha_inicio
              AND (
                  a.fecha_vencimiento IS NULL
                  OR n.fecha_inicio <= a.fecha_vencimiento
              )
          )

          OR

          -- Nueva recurrente / existente no recurrente
          (
              n.recurrente = TRUE
              AND a.recurrente = FALSE
              AND EXTRACT(
                    ISODOW FROM a.fecha_inicio
                  )::INTEGER = n.dia_semana
              AND a.fecha_inicio >= n.fecha_inicio
              AND (
                  n.fecha_vencimiento IS NULL
                  OR a.fecha_inicio <= n.fecha_vencimiento
              )
          )
      )

      -- =====================================================
      -- SOLAPAMIENTO DE HORARIOS
      -- =====================================================
      AND n.hora <
            a.hora + (
                a.duracion_minutos * INTERVAL '1 minute'
            )
      AND n.fin > a.hora;
$$;