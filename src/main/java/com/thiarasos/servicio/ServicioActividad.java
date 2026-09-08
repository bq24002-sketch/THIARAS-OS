package com.thiarasos.servicio;

import com.thiarasos.persistencia.EstadoActividad;
import com.thiarasos.persistencia.EstadoAplicacion;
import com.thiarasos.persistencia.RepositorioActividad;
import com.thiarasos.persistencia.RepositorioDashboard;
import com.thiarasos.persistencia.RepositorioNotificacion;
import com.thiarasos.persistencia.NuevaActividad;
import com.thiarasos.persistencia.TipoActividadBaseDatos;

import java.util.List;

public final class ServicioActividad {

    private final RepositorioActividad actividades;
    private final RepositorioDashboard dashboard;
    private final RepositorioNotificacion notificaciones;

    public ServicioActividad(
            RepositorioActividad actividades,
            RepositorioDashboard dashboard,
            RepositorioNotificacion notificaciones
    ) {
        this.actividades = actividades;
        this.dashboard = dashboard;
        this.notificaciones = notificaciones;
    }

    public EstadoAplicacion iniciarYRefrescar(long actividadId) {
        actividades.iniciar(actividadId);
        return refrescar();
    }

    public EstadoAplicacion completarYRefrescar(long actividadId) {
        actividades.completar(actividadId);
        return refrescar();
    }

    public EstadoAplicacion marcarNotificacionLeidaYRefrescar(long notificacionId) {
        notificaciones.marcarComoLeida(notificacionId);
        return refrescar();
    }

    public EstadoActividad obtenerEstado(long actividadId) {
        return actividades.obtenerEstado(actividadId);
    }

    public List<TipoActividadBaseDatos> listarTipos() {
        return actividades.listarTipos();
    }

    public EstadoAplicacion crearYRefrescar(NuevaActividad actividad) {
        actividades.crear(actividad);
        return refrescar();
    }

    public EstadoAplicacion refrescar() {
        return new EstadoAplicacion(
                actividades.listarParaInterfaz(),
                dashboard.obtenerResumen(),
                notificaciones.listarPendientes()
        );
    }
}
