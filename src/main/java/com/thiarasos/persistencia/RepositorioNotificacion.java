package com.thiarasos.persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class RepositorioNotificacion {

    private static final String CONSULTA_NOTIFICACIONES = ConsultaSql.cargar("30_notificaciones_pendientes.sql");

    private final ProveedorConexion proveedorConexion;

    public RepositorioNotificacion(ProveedorConexion proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    public List<NotificacionPendiente> listarPendientes() {
        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(CONSULTA_NOTIFICACIONES);
             ResultSet resultado = consulta.executeQuery()) {
            List<NotificacionPendiente> notificaciones = new ArrayList<>();

            while (resultado.next()) {
                notificaciones.add(new NotificacionPendiente(
                        resultado.getLong("id"),
                        resultado.getString("tipo"),
                        resultado.getString("titulo"),
                        resultado.getString("mensaje"),
                        obtenerLong(resultado, "actividad_id"),
                        obtenerLong(resultado, "logro_id"),
                        resultado.getObject("creada_en", LocalDateTime.class),
                        resultado.getBoolean("leida")
                ));
            }

            return List.copyOf(notificaciones);
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudieron cargar las notificaciones", e);
        }
    }

    public void marcarComoLeida(long notificacionId) {
        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(
                     "SELECT marcar_notificacion_leida(?)"
             )) {
            consulta.setLong(1, notificacionId);
            consulta.execute();
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo marcar la notificacion como leida", e);
        }
    }

    private Long obtenerLong(ResultSet resultado, String columna) throws SQLException {
        long valor = resultado.getLong(columna);
        return resultado.wasNull() ? null : valor;
    }
}
