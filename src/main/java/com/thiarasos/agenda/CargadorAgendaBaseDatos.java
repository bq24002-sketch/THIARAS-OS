package com.thiarasos.agenda;

import com.thiarasos.persistencia.ErrorPersistencia;
import com.thiarasos.persistencia.ProveedorConexion;
import com.thiarasos.persistencia.Recordatorio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CargadorAgendaBaseDatos implements ProveedorAgenda {

    private static final String CONSULTA = """
            SELECT a.id,
                   a.titulo,
                   a.materia,
                   a.contenido,
                   a.duracion_minutos,
                   t.nombre AS tipo,
                   a.hora,
                   r.id AS recordatorio_id,
                   r.minutos_anticipacion,
                   r.activo AS recordatorio_activo
            FROM actividad a
            JOIN tipo_actividad t
                ON t.id = a.tipo_id
            JOIN recordatorio r
                ON r.actividad_id = a.id
            LEFT JOIN recurrencia re
                ON re.actividad_id = a.id
            WHERE a.activa = TRUE
              AND r.activo = TRUE
              AND a.hora IS NOT NULL
              AND (
                    (
                        a.recurrente = TRUE
                        AND re.dia_semana =
                            EXTRACT(ISODOW FROM CURRENT_DATE)
                        AND a.fecha_inicio <= CURRENT_DATE
                        AND (
                            re.fecha_fin IS NULL
                            OR re.fecha_fin >= CURRENT_DATE
                        )
                    )
                    OR
                    (
                        a.recurrente = FALSE
                        AND CURRENT_DATE BETWEEN a.fecha_inicio
                            AND COALESCE(
                                a.fecha_vencimiento,
                                a.fecha_inicio
                            )
                    )
              )
            ORDER BY a.hora, r.minutos_anticipacion DESC
            """;

    private final ProveedorConexion proveedorConexion;

    public CargadorAgendaBaseDatos(ProveedorConexion proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    @Override
    public Agenda cargar() {

        Agenda agenda = new Agenda();

        Map<Long, DatosActividad> actividades =
                new LinkedHashMap<>();

        try (
                var conexion = proveedorConexion.abrir();
                PreparedStatement consulta =
                        conexion.prepareStatement(CONSULTA);
                ResultSet resultado = consulta.executeQuery()
        ) {

            while (resultado.next()) {

                long actividadId =
                        resultado.getLong("id");

                DatosActividad datos =
                        actividades.get(actividadId);

                if (datos == null) {

                    Integer duracion =
                            resultado.getObject(
                                    "duracion_minutos",
                                    Integer.class
                            );

                    datos = new DatosActividad(
                            actividadId,
                            resultado.getObject(
                                    "hora",
                                    LocalTime.class
                            ),
                            resultado.getString("materia"),
                            resultado.getString("titulo"),
                            resultado.getString("contenido"),
                            duracion,
                            tipo(
                                    resultado.getString("tipo")
                            )
                    );

                    actividades.put(
                            actividadId,
                            datos
                    );
                }

                datos.recordatorios.add(
                        new Recordatorio(
                                resultado.getLong(
                                        "recordatorio_id"
                                ),
                                resultado.getInt(
                                        "minutos_anticipacion"
                                ),
                                resultado.getBoolean(
                                        "recordatorio_activo"
                                )
                        )
                );
            }

            for (DatosActividad datos :
                    actividades.values()) {

                agenda.agregar(
                        new Actividad(
                                datos.id,
                                LocalDate.now().getDayOfWeek(),
                                datos.hora,
                                datos.materia,
                                datos.titulo,
                                datos.contenido,
                                datos.duracionMinutos == null
                                        ? "Sin duracion definida"
                                        : datos.duracionMinutos + " min",
                                datos.tipo,
                                datos.recordatorios
                        )
                );
            }

            return agenda;

        } catch (SQLException e) {

            throw new ErrorPersistencia(
                    "No se pudo cargar la agenda de recordatorios",
                    e
            );
        }
    }

    private TipoActividad tipo(String nombre) {

        try {

            return TipoActividad.valueOf(nombre);

        } catch (IllegalArgumentException e) {

            return TipoActividad.PERSONAL;
        }
    }

    private static final class DatosActividad {

        private final long id;
        private final LocalTime hora;
        private final String materia;
        private final String titulo;
        private final String contenido;
        private final Integer duracionMinutos;
        private final TipoActividad tipo;
        private final List<Recordatorio> recordatorios =
                new ArrayList<>();

        private DatosActividad(
                long id,
                LocalTime hora,
                String materia,
                String titulo,
                String contenido,
                Integer duracionMinutos,
                TipoActividad tipo
        ) {

            this.id = id;
            this.hora = hora;
            this.materia = materia;
            this.titulo = titulo;
            this.contenido = contenido;
            this.duracionMinutos = duracionMinutos;
            this.tipo = tipo;
        }
    }
}