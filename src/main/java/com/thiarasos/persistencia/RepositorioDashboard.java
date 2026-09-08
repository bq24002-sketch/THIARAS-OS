package com.thiarasos.persistencia;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class RepositorioDashboard {

    private static final String CONSULTA_DASHBOARD = ConsultaSql.cargar("31_dashboard.sql");

    private final ProveedorConexion proveedorConexion;

    public RepositorioDashboard(ProveedorConexion proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    public ResumenDashboard obtenerResumen() {
        try (var conexion = proveedorConexion.abrir();
             var consulta = conexion.createStatement()) {
            boolean tieneResultado = consulta.execute(CONSULTA_DASHBOARD);

            if (!tieneResultado) {
                throw new ErrorPersistencia("La consulta del dashboard no devolvio un resumen", null);
            }

            try (ResultSet resultado = consulta.getResultSet()) {
                if (!resultado.next()) {
                    throw new ErrorPersistencia("La consulta del dashboard no devolvio datos", null);
                }

                return new ResumenDashboard(
                        resultado.getInt("actividades_hoy"),
                        resultado.getInt("minutos_hoy"),
                        resultado.getInt("actividades_semana"),
                        resultado.getInt("minutos_semana")
                );
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo cargar el dashboard", e);
        }
    }
}
