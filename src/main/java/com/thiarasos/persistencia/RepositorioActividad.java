package com.thiarasos.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class RepositorioActividad {

    private static final String CONSULTA_ACTIVIDADES = ConsultaSql.cargar("32_actividades_interfaz.sql");
    
    private static final String CONSULTA_CONFLICTO = """
        SELECT actividad_id, titulo, dia_semana, inicio, fin
        FROM existe_conflicto_horario(?, ?, ?, ?, ?, ?)
        LIMIT 1
        """;
    private static final String CONSULTA_HORARIOS_OCUPADOS = """
    SELECT
        a.id AS actividad_id,
        a.titulo,
        COALESCE(r.dia_semana, EXTRACT(ISODOW FROM a.fecha_inicio)::integer) AS dia_semana,
        a.hora AS inicio,
        a.hora + (a.duracion_minutos * INTERVAL '1 minute') AS fin
    FROM actividad a
    LEFT JOIN recurrencia r
        ON r.actividad_id = a.id
    WHERE a.activa = TRUE
      AND a.hora IS NOT NULL
      AND a.duracion_minutos IS NOT NULL
      AND (
          (
              a.recurrente = TRUE
              AND r.dia_semana = EXTRACT(ISODOW FROM ?::date)::integer
          )
          OR
          (
              a.recurrente = FALSE
              AND a.fecha_inicio <= ?::date
              AND COALESCE(a.fecha_vencimiento, a.fecha_inicio) >= ?::date
          )
      )
    ORDER BY a.hora, a.id
    """;
    private final ProveedorConexion proveedorConexion;

    public RepositorioActividad(ProveedorConexion proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    public long iniciar(long actividadId) {
        return ejecutarFuncionConResultado("SELECT iniciar_actividad(?)", actividadId);
    }

    public long completar(long actividadId) {
        return ejecutarFuncionConResultado("SELECT completar_actividad(?)", actividadId);
    }

    public EstadoActividad obtenerEstado(long actividadId) {
        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(
                     "SELECT * FROM estado_actividad(?)"
             )) {
            consulta.setLong(1, actividadId);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    throw new ErrorPersistencia("No se encontro el estado de la actividad " + actividadId, null);
                }

                return mapearEstado(resultado);
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo consultar el estado de la actividad", e);
        }
    }

    public List<ActividadInterfaz> listarParaInterfaz() {
        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(CONSULTA_ACTIVIDADES);
             ResultSet resultado = consulta.executeQuery()) {
            List<ActividadInterfaz> actividades = new ArrayList<>();

            while (resultado.next()) {
                actividades.add(mapearActividad(resultado));
            }

            return List.copyOf(actividades);
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudieron cargar las actividades", e);
        }
    }

    public List<TipoActividadBaseDatos> listarTipos() {
        String sql = "SELECT id, nombre FROM tipo_actividad ORDER BY nombre";

        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {
            List<TipoActividadBaseDatos> tipos = new ArrayList<>();
            while (resultado.next()) {
                tipos.add(new TipoActividadBaseDatos(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                ));
            }
            return List.copyOf(tipos);
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudieron cargar los tipos de actividad", e);
        }
    }
    
    public List<BloqueHorarioOcupado> listarHorariosOcupados(LocalDate fecha) {
    try (var conexion = proveedorConexion.abrir();
         PreparedStatement consulta = conexion.prepareStatement(CONSULTA_HORARIOS_OCUPADOS)) {

        consulta.setObject(1, fecha);
        consulta.setObject(2, fecha);
        consulta.setObject(3, fecha);

        List<BloqueHorarioOcupado> bloques = new ArrayList<>();

        try (ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                bloques.add(new BloqueHorarioOcupado(
                        resultado.getLong("actividad_id"),
                        resultado.getString("titulo"),
                        resultado.getInt("dia_semana"),
                        resultado.getObject("inicio", LocalTime.class),
                        resultado.getObject("fin", LocalTime.class)
                ));
            }
        }

        return List.copyOf(bloques);

    } catch (SQLException e) {
        throw new ErrorPersistencia(
                "No se pudieron consultar los horarios ocupados",
                e
        );
    }
}
    public boolean existeConflictoHorario(NuevaActividad actividad) {
    if (actividad.hora() == null || actividad.duracionMinutos() == null) {
        return false;
    }

    try (var conexion = proveedorConexion.abrir();
         PreparedStatement consulta = conexion.prepareStatement(CONSULTA_CONFLICTO)) {

        consulta.setObject(1, actividad.fechaInicio());
        consulta.setObject(2, actividad.fechaVencimiento());
        consulta.setObject(3, actividad.hora());
        consulta.setInt(4, actividad.duracionMinutos());
        consulta.setBoolean(5, actividad.recurrente());

        if (actividad.recurrente()) {
            consulta.setInt(6, actividad.diaSemana());
        } else {
            consulta.setNull(6, java.sql.Types.INTEGER);
        }

        try (ResultSet resultado = consulta.executeQuery()) {
            return resultado.next();
        }

    } catch (SQLException e) {
        throw new ErrorPersistencia(
                "No se pudo comprobar la disponibilidad del horario",
                e
        );
    }
}

public ActividadEditable obtenerParaEdicion(long actividadId) {

    String sql = """
        SELECT
            a.id,
            a.titulo,
            a.materia,
            a.contenido,
            a.duracion_minutos,
            ta.nombre AS tipo,
            a.fecha_inicio,
            a.fecha_vencimiento,
            a.hora,
            a.recurrente,
            COALESCE(r.dia_semana, 0) AS dia_semana,
            COALESCE(
                (
                    SELECT rec.minutos_anticipacion
                    FROM recordatorio rec
                    WHERE rec.actividad_id = a.id
                      AND rec.activo = TRUE
                    ORDER BY rec.id DESC
                    LIMIT 1
                ),
                0
            ) AS minutos_anticipacion
        FROM actividad a
        JOIN tipo_actividad ta
            ON ta.id = a.tipo_id
        LEFT JOIN recurrencia r
            ON r.actividad_id = a.id
        WHERE a.id = ?
          AND a.activa = TRUE
        """;

    try (var conexion = proveedorConexion.abrir();
         PreparedStatement consulta = conexion.prepareStatement(sql)) {

        consulta.setLong(1, actividadId);

        try (ResultSet resultado = consulta.executeQuery()) {

            if (!resultado.next()) {
                throw new ErrorPersistencia(
                        "No se encontro la actividad " + actividadId,
                        null
                );
            }

            return new ActividadEditable(
                    resultado.getLong("id"),
                    resultado.getString("titulo"),
                    resultado.getString("materia"),
                    resultado.getString("contenido"),
                    obtenerEntero(resultado, "duracion_minutos"),
                    resultado.getString("tipo"),
                    resultado.getObject("fecha_inicio", LocalDate.class),
                    resultado.getObject("fecha_vencimiento", LocalDate.class),
                    resultado.getObject("hora", LocalTime.class),
                    resultado.getBoolean("recurrente"),
                    resultado.getInt("dia_semana"),
                    resultado.getInt("minutos_anticipacion")
            );
        }

    } catch (SQLException e) {
        throw new ErrorPersistencia(
                "No se pudo cargar la actividad para editar",
                e
        );
    }
}

    public void modificar(long actividadId, NuevaActividad actividad) {

    String sqlActividad = """
        UPDATE actividad
        SET
            titulo = ?,
            materia = ?,
            contenido = ?,
            duracion_minutos = ?,
            tipo_id = (
                SELECT id
                FROM tipo_actividad
                WHERE nombre = ?
            ),
            fecha_inicio = ?,
            fecha_vencimiento = ?,
            hora = ?,
            recurrente = ?
        WHERE id = ?
        """;

    String sqlEliminarRecurrencia = """
        DELETE FROM recurrencia
        WHERE actividad_id = ?
        """;

    String sqlCrearRecurrencia = """
        INSERT INTO recurrencia (
            actividad_id,
            tipo,
            intervalo,
            dia_semana
        )
        VALUES (?, 'SEMANAL', 1, ?)
        """;

    String sqlRecordatorio = """
        UPDATE recordatorio
        SET minutos_anticipacion = ?,
            activo = TRUE
        WHERE actividad_id = ?
        """;

    String sqlCrearRecordatorio = """
        INSERT INTO recordatorio (
            actividad_id,
            minutos_anticipacion,
            activo
        )
        SELECT ?, ?, TRUE
        WHERE NOT EXISTS (
            SELECT 1
            FROM recordatorio
            WHERE actividad_id = ?
        )
        """;

    try (var conexion = proveedorConexion.abrir()) {

        conexion.setAutoCommit(false);

        try (
            PreparedStatement actualizarActividad =
                    conexion.prepareStatement(sqlActividad);

            PreparedStatement eliminarRecurrencia =
                    conexion.prepareStatement(sqlEliminarRecurrencia);

            PreparedStatement crearRecurrencia =
                    conexion.prepareStatement(sqlCrearRecurrencia);

            PreparedStatement actualizarRecordatorio =
                    conexion.prepareStatement(sqlRecordatorio);

            PreparedStatement crearRecordatorio =
                    conexion.prepareStatement(sqlCrearRecordatorio)
        ) {

            // ============================================
            // ACTIVIDAD
            // ============================================

            actualizarActividad.setString(1, actividad.titulo());
            actualizarActividad.setString(2, actividad.materia());
            actualizarActividad.setString(3, actividad.contenido());

            if (actividad.duracionMinutos() == null) {
                actualizarActividad.setNull(
                        4,
                        java.sql.Types.INTEGER
                );
            } else {
                actualizarActividad.setInt(
                        4,
                        actividad.duracionMinutos()
                );
            }

            actualizarActividad.setString(
                    5,
                    actividad.tipo()
            );

            actualizarActividad.setObject(
                    6,
                    actividad.fechaInicio()
            );

            actualizarActividad.setObject(
                    7,
                    actividad.fechaVencimiento()
            );

            actualizarActividad.setObject(
                    8,
                    actividad.hora()
            );

            actualizarActividad.setBoolean(
                    9,
                    actividad.recurrente()
            );

            actualizarActividad.setLong(
                    10,
                    actividadId
            );

            int modificadas = actualizarActividad.executeUpdate();

            if (modificadas == 0) {
                throw new ErrorPersistencia(
                        "No existe la actividad " + actividadId,
                        null
                );
            }

            // ============================================
            // RECURRENCIA
            // ============================================

            eliminarRecurrencia.setLong(
                    1,
                    actividadId
            );

            eliminarRecurrencia.executeUpdate();

            if (actividad.recurrente()) {

                crearRecurrencia.setLong(
                        1,
                        actividadId
                );

                crearRecurrencia.setInt(
                        2,
                        actividad.diaSemana()
                );

                crearRecurrencia.executeUpdate();
            }

            // ============================================
            // RECORDATORIO
            // ============================================

            actualizarRecordatorio.setInt(
                    1,
                    actividad.minutosAnticipacion()
            );

            actualizarRecordatorio.setLong(
                    2,
                    actividadId
            );

            int recordatoriosModificados =
                    actualizarRecordatorio.executeUpdate();

            if (recordatoriosModificados == 0) {

                crearRecordatorio.setLong(
                        1,
                        actividadId
                );

                crearRecordatorio.setInt(
                        2,
                        actividad.minutosAnticipacion()
                );

                crearRecordatorio.setLong(
                        3,
                        actividadId
                );

                crearRecordatorio.executeUpdate();
            }

            conexion.commit();

        } catch (Exception e) {

            try {
                conexion.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }

            if (e instanceof ErrorPersistencia errorPersistencia) {
                throw errorPersistencia;
            }

            throw new ErrorPersistencia(
                    "No se pudo modificar la actividad",
                    e
            );

        } finally {
            conexion.setAutoCommit(true);
        }

    } catch (SQLException e) {

        throw new ErrorPersistencia(
                "No se pudo modificar la actividad",
                e
        );
    }
}
    public long crear(NuevaActividad actividad) {
        String crearActividad = """
                INSERT INTO actividad (
                    titulo, materia, contenido, duracion_minutos, tipo_id,
                    fecha_inicio, fecha_vencimiento, hora, recurrente, activa
                )
                SELECT ?, ?, ?, ?, id, ?, ?, ?, ?, TRUE
                FROM tipo_actividad
                WHERE nombre = ?
                RETURNING id
                """;
        String crearRecordatorio = """
                INSERT INTO recordatorio (actividad_id, minutos_anticipacion, activo)
                VALUES (?, ?, TRUE)
                """;
        String crearRecurrencia = """
                INSERT INTO recurrencia (actividad_id, tipo, intervalo, dia_semana)
                VALUES (?, 'SEMANAL', 1, ?)
                """;

        try (var conexion = proveedorConexion.abrir()) {
    conexion.setAutoCommit(false);
    try {

        validarConflictoHorario(conexion, actividad);

        long actividadId;
                try (PreparedStatement consulta = conexion.prepareStatement(crearActividad)) {
                    consulta.setString(1, actividad.titulo());
                    consulta.setString(2, actividad.materia());
                    consulta.setString(3, actividad.contenido());
                    if (actividad.duracionMinutos() == null) {
                        consulta.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        consulta.setInt(4, actividad.duracionMinutos());
                    }
                    consulta.setObject(5, actividad.fechaInicio());
                    consulta.setObject(6, actividad.fechaVencimiento());
                    consulta.setObject(7, actividad.hora());
                    consulta.setBoolean(8, actividad.recurrente());
                    consulta.setString(9, actividad.tipo());

                    try (ResultSet resultado = consulta.executeQuery()) {
                        if (!resultado.next()) {
                            throw new SQLException("No se creo la actividad: tipo inexistente.");
                        }
                        actividadId = resultado.getLong(1);
                    }
                }

                try (PreparedStatement consulta = conexion.prepareStatement(crearRecordatorio)) {
                    consulta.setLong(1, actividadId);
                    consulta.setInt(2, actividad.minutosAnticipacion());
                    consulta.executeUpdate();
                }

                if (actividad.recurrente()) {
                    try (PreparedStatement consulta = conexion.prepareStatement(crearRecurrencia)) {
                        consulta.setLong(1, actividadId);
                        consulta.setInt(2, actividad.diaSemana());
                        consulta.executeUpdate();
                    }
                }

                conexion.commit();
                return actividadId;
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo crear la actividad", e);
        }
    }
    private void validarConflictoHorario(
        java.sql.Connection conexion,
        NuevaActividad actividad
) throws SQLException {

    if (actividad.hora() == null || actividad.duracionMinutos() == null) {
        return;
    }

    try (PreparedStatement consulta = conexion.prepareStatement(CONSULTA_CONFLICTO)) {

        consulta.setObject(1, actividad.fechaInicio());
        consulta.setObject(2, actividad.fechaVencimiento());
        consulta.setObject(3, actividad.hora());
        consulta.setInt(4, actividad.duracionMinutos());
        consulta.setBoolean(5, actividad.recurrente());

        if (actividad.recurrente()) {
            consulta.setInt(6, actividad.diaSemana());
        } else {
            consulta.setNull(6, java.sql.Types.INTEGER);
        }

        try (ResultSet resultado = consulta.executeQuery()) {
            if (resultado.next()) {
                String titulo = resultado.getString("titulo");
                LocalTime inicio = resultado.getObject("inicio", LocalTime.class);
                LocalTime fin = resultado.getObject("fin", LocalTime.class);

                throw new ErrorPersistencia(
                        "Horario ocupado: la actividad se cruza con \""
                                + titulo
                                + "\" ("
                                + inicio
                                + " - "
                                + fin
                                + ").",
                        null
                );
            }
        }
    }
}
    private long ejecutarFuncionConResultado(String funcion, long actividadId) {
        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(funcion)) {
            consulta.setLong(1, actividadId);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    throw new ErrorPersistencia("La funcion no devolvio un cumplimiento", null);
                }

                return resultado.getLong(1);
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo actualizar la actividad", e);
        }
    }

    private EstadoActividad mapearEstado(ResultSet resultado) throws SQLException {
        return new EstadoActividad(
                resultado.getLong("actividad_id"),
                resultado.getString("titulo"),
                obtenerEntero(resultado, "duracion_minutos"),
                resultado.getString("estado"),
                obtenerLong(resultado, "cumplimiento_id"),
                resultado.getObject("momento_inicio", LocalDateTime.class),
                resultado.getObject("momento_fin", LocalDateTime.class),
                obtenerEntero(resultado, "minutos_realizados")
        );
    }

    private ActividadInterfaz mapearActividad(ResultSet resultado) throws SQLException {
        return new ActividadInterfaz(
                resultado.getLong("id"),
                resultado.getString("titulo"),
                resultado.getString("materia"),
                resultado.getString("contenido"),
                obtenerEntero(resultado, "duracion_minutos"),
                resultado.getObject("fecha_inicio", LocalDate.class),
                resultado.getObject("fecha_vencimiento", LocalDate.class),
                resultado.getObject("hora", LocalTime.class),
                resultado.getBoolean("recurrente"),
                resultado.getBoolean("activa"),
                resultado.getString("estado"),
                obtenerLong(resultado, "cumplimiento_id"),
                resultado.getObject("momento_inicio", LocalDateTime.class),
                resultado.getObject("momento_fin", LocalDateTime.class),
                obtenerEntero(resultado, "minutos_realizados")
        );
    }

    private Long obtenerLong(ResultSet resultado, String columna) throws SQLException {
        long valor = resultado.getLong(columna);
        return resultado.wasNull() ? null : valor;
    }

    private Integer obtenerEntero(ResultSet resultado, String columna) throws SQLException {
        int valor = resultado.getInt(columna);
        return resultado.wasNull() ? null : valor;
    }
}
