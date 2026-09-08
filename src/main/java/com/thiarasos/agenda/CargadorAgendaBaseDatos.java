package com.thiarasos.agenda;

import com.thiarasos.persistencia.ErrorPersistencia;
import com.thiarasos.persistencia.ProveedorConexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public final class CargadorAgendaBaseDatos implements ProveedorAgenda {

    private static final String CONSULTA = """
            SELECT a.id, a.titulo, a.materia, a.contenido, a.duracion_minutos,
                   t.nombre AS tipo, a.hora, r.minutos_anticipacion
            FROM actividad a
            JOIN tipo_actividad t ON t.id = a.tipo_id
            JOIN recordatorio r ON r.actividad_id = a.id
            LEFT JOIN recurrencia re ON re.actividad_id = a.id
            WHERE a.activa = TRUE AND r.activo = TRUE AND a.hora IS NOT NULL
              AND ((a.recurrente = TRUE
                    AND re.dia_semana = EXTRACT(ISODOW FROM CURRENT_DATE)
                    AND a.fecha_inicio <= CURRENT_DATE
                    AND (re.fecha_fin IS NULL OR re.fecha_fin >= CURRENT_DATE))
                   OR (a.recurrente = FALSE
                       AND CURRENT_DATE BETWEEN a.fecha_inicio
                           AND COALESCE(a.fecha_vencimiento, a.fecha_inicio)))
            ORDER BY a.hora, r.minutos_anticipacion DESC
            """;

    private final ProveedorConexion proveedorConexion;

    public CargadorAgendaBaseDatos(ProveedorConexion proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    @Override
    public Agenda cargar() {
        Agenda agenda = new Agenda();
        try (var conexion = proveedorConexion.abrir();
             PreparedStatement consulta = conexion.prepareStatement(CONSULTA);
             ResultSet resultado = consulta.executeQuery()) {
            while (resultado.next()) {
                Integer minutos = resultado.getObject("duracion_minutos", Integer.class);
                agenda.agregar(new Actividad(
                        resultado.getLong("id"), LocalDate.now().getDayOfWeek(),
                        resultado.getObject("hora", LocalTime.class), resultado.getString("materia"),
                        resultado.getString("titulo"), resultado.getString("contenido"),
                        minutos == null ? "Sin duracion definida" : minutos + " min",
                        tipo(resultado.getString("tipo")), resultado.getInt("minutos_anticipacion")
                ));
            }
            return agenda;
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo cargar la agenda de recordatorios", e);
        }
    }

    private TipoActividad tipo(String nombre) {
        try {
            return TipoActividad.valueOf(nombre);
        } catch (IllegalArgumentException e) {
            return TipoActividad.PERSONAL;
        }
    }
}
