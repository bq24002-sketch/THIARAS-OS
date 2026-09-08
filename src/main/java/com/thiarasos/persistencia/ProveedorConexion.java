package com.thiarasos.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ProveedorConexion {

    Connection abrir() throws SQLException;
}
