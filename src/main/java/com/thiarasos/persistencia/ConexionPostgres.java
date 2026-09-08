package com.thiarasos.persistencia;

import com.thiarasos.config.Configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionPostgres implements ProveedorConexion {

    private static final String VARIABLE_CONTRASENA = "THIARAS_DB_PASSWORD";

    private final String url;
    private final String usuario;
    private final String contrasena;

    public ConexionPostgres(Configuracion configuracion) {
        this.url = configuracion.obtener("DB_URL");
        this.usuario = configuracion.obtener("DB_USUARIO");
        this.contrasena = obtenerContrasena(configuracion);
    }

    @Override
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private String obtenerContrasena(Configuracion configuracion) {
        String contrasenaEntorno = System.getenv(VARIABLE_CONTRASENA);

        if (contrasenaEntorno != null && !contrasenaEntorno.isBlank()) {
            return contrasenaEntorno;
        }

        return configuracion.obtener("DB_CONTRASENA", "");
    }
}
