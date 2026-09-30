package com.thiarasos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDiario {

    private final ProveedorConexion proveedorConexion;

    private static final String INSERTAR = """
        INSERT INTO diario (
            actividad_id,
            cumplimiento_id,
            fecha,
            contenido
        )
        VALUES (?, ?, ?, ?)
        RETURNING id, actividad_id, cumplimiento_id, fecha, contenido, creado_en
        """;

    private static final String CONSULTAR_POR_ACTIVIDAD = """
        SELECT
            d.id,
            d.actividad_id,
            d.cumplimiento_id,
            d.fecha,
            d.contenido,
            d.creado_en
        FROM diario d
        WHERE d.actividad_id = ?
        ORDER BY d.fecha DESC, d.creado_en DESC
        """;

    private static final String CONSULTAR_TODAS = """
        SELECT
            d.id,
            d.actividad_id,
            d.cumplimiento_id,
            d.fecha,
            d.contenido,
            d.creado_en
        FROM diario d
        ORDER BY d.fecha DESC, d.creado_en DESC
        """;

    private static final String ELIMINAR = """
        DELETE FROM diario
        WHERE id = ?
        """;

    public RepositorioDiario(ProveedorConexion proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    public EntradaDiario crear(
            long actividadId,
            Long cumplimientoId,
            LocalDate fecha,
            String contenido
    ) throws ErrorPersistencia {

        try (Connection conexion = proveedorConexion.abrir();
             PreparedStatement sentencia =
                     conexion.prepareStatement(INSERTAR)) {

            sentencia.setLong(1, actividadId);

            if (cumplimientoId == null) {
                sentencia.setNull(2, Types.BIGINT);
            } else {
                sentencia.setLong(2, cumplimientoId);
            }

            sentencia.setObject(3, fecha);
            sentencia.setString(4, contenido);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (!resultado.next()) {
                    throw new ErrorPersistencia(
                            "No se pudo crear la entrada del diario.",
                            null
                    );
                }

                return mapear(resultado);
            }

        } catch (SQLException e) {
            throw new ErrorPersistencia(
                    "Error al crear la entrada del diario.",
                    e
            );
        }
    }

    public List<EntradaDiario> obtenerPorActividad(
            long actividadId
    ) throws ErrorPersistencia {

        List<EntradaDiario> entradas = new ArrayList<>();

        try (Connection conexion = proveedorConexion.abrir();
             PreparedStatement sentencia =
                     conexion.prepareStatement(CONSULTAR_POR_ACTIVIDAD)) {

            sentencia.setLong(1, actividadId);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {
                    entradas.add(mapear(resultado));
                }
            }

            return entradas;

        } catch (SQLException e) {
            throw new ErrorPersistencia(
                    "Error al consultar el diario de la actividad.",
                    e
            );
        }
    }

    public List<EntradaDiario> obtenerTodas()
            throws ErrorPersistencia {

        List<EntradaDiario> entradas = new ArrayList<>();

        try (Connection conexion = proveedorConexion.abrir();
             PreparedStatement sentencia =
                     conexion.prepareStatement(CONSULTAR_TODAS);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                entradas.add(mapear(resultado));
            }

            return entradas;

        } catch (SQLException e) {
            throw new ErrorPersistencia(
                    "Error al consultar el diario.",
                    e
            );
        }
    }

    public void eliminar(long id)
            throws ErrorPersistencia {

        try (Connection conexion = proveedorConexion.abrir();
             PreparedStatement sentencia =
                     conexion.prepareStatement(ELIMINAR)) {

            sentencia.setLong(1, id);
            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new ErrorPersistencia(
                    "Error al eliminar la entrada del diario.",
                    e
            );
        }
    }

    private EntradaDiario mapear(ResultSet resultado)
            throws SQLException {

        long cumplimientoId =
                resultado.getLong("cumplimiento_id");

        Long cumplimiento = resultado.wasNull()
                ? null
                : cumplimientoId;

        return new EntradaDiario(
                resultado.getLong("id"),
                resultado.getLong("actividad_id"),
                cumplimiento,
                resultado.getObject(
                        "fecha",
                        LocalDate.class
                ),
                resultado.getString("contenido"),
                resultado.getObject(
                        "creado_en",
                        LocalDateTime.class
                )
        );
    }
}