package com.thiarasos.consola;

import com.thiarasos.persistencia.ActividadInterfaz;
import com.thiarasos.persistencia.ErrorPersistencia;
import com.thiarasos.persistencia.EstadoActividad;
import com.thiarasos.persistencia.EstadoAplicacion;
import com.thiarasos.persistencia.NotificacionPendiente;
import com.thiarasos.persistencia.ResumenDashboard;
import com.thiarasos.servicio.ServicioActividad;

public final class ConsolaBaseDatos {

    private final ServicioActividad servicioActividad;

    public ConsolaBaseDatos(ServicioActividad servicioActividad) {
        this.servicioActividad = servicioActividad;
    }

    public void ejecutar(String[] argumentos) {
        if (argumentos.length == 0 || "ayuda".equals(argumentos[0])) {
            mostrarAyuda();
            return;
        }

        try {
            switch (argumentos[0]) {
                case "dashboard" -> mostrarEstado(servicioActividad.refrescar());
                case "iniciar" -> mostrarEstado(servicioActividad.iniciarYRefrescar(obtenerId(argumentos)));
                case "completar" -> mostrarEstado(servicioActividad.completarYRefrescar(obtenerId(argumentos)));
                case "estado" -> mostrarActividad(servicioActividad.obtenerEstado(obtenerId(argumentos)));
                case "leer" -> mostrarEstado(
                        servicioActividad.marcarNotificacionLeidaYRefrescar(obtenerId(argumentos))
                );
                default -> mostrarAyuda();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            mostrarAyuda();
        } catch (ErrorPersistencia e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                System.err.println(e.getCause().getMessage());
            }
        }
    }

    private long obtenerId(String[] argumentos) {
        if (argumentos.length != 2) {
            throw new IllegalArgumentException("Se requiere un identificador numerico.");
        }

        try {
            return Long.parseLong(argumentos[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El identificador debe ser numerico.", e);
        }
    }

    private void mostrarEstado(EstadoAplicacion estado) {
        ResumenDashboard resumen = estado.resumenDashboard();

        System.out.printf(
                "Hoy: %d actividades, %d minutos | Semana: %d actividades, %d minutos%n%n",
                resumen.actividadesHoy(),
                resumen.minutosHoy(),
                resumen.actividadesSemana(),
                resumen.minutosSemana()
        );

        System.out.println("ACTIVIDADES");
        for (ActividadInterfaz actividad : estado.actividades()) {
            System.out.printf("%d | %s | %s%n", actividad.id(), actividad.estado(), actividad.titulo());
        }

        System.out.println("\nNOTIFICACIONES PENDIENTES");
        for (NotificacionPendiente notificacion : estado.notificacionesPendientes()) {
            System.out.printf("%d | %s | %s%n", notificacion.id(), notificacion.tipo(), notificacion.titulo());
        }
    }

    private void mostrarActividad(EstadoActividad actividad) {
        System.out.printf(
                "%d | %s | %s | cumplimiento: %s%n",
                actividad.actividadId(),
                actividad.estado(),
                actividad.titulo(),
                actividad.cumplimientoId()
        );
    }

    private void mostrarAyuda() {
        System.out.println("Uso: --db <dashboard|iniciar|completar|estado|leer> [id]");
    }
}
