package com.thiarasos.persistencia;

import java.util.List;

public record EstadoAplicacion(
        List<ActividadInterfaz> actividades,
        ResumenDashboard resumenDashboard,
        List<NotificacionPendiente> notificacionesPendientes
) {
}
