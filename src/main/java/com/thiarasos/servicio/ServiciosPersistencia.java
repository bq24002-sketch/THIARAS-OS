package com.thiarasos.servicio;

import com.thiarasos.config.Configuracion;
import com.thiarasos.persistencia.ConexionPostgres;
import com.thiarasos.persistencia.RepositorioActividad;
import com.thiarasos.persistencia.RepositorioDashboard;
import com.thiarasos.persistencia.RepositorioNotificacion;

public final class ServiciosPersistencia {

    private ServiciosPersistencia() {
    }

    public static ServicioActividad crearServicioActividad(Configuracion configuracion) {
        ConexionPostgres conexion = new ConexionPostgres(configuracion);

        return new ServicioActividad(
                new RepositorioActividad(conexion),
                new RepositorioDashboard(conexion),
                new RepositorioNotificacion(conexion)
        );
    }
}
