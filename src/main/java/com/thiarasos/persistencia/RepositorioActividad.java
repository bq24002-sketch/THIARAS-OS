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
