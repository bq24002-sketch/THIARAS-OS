package com.thiarasos.persistencia;

import java.time.LocalDateTime;

public record NotificacionPendiente(
        long id,
        String tipo,
        String titulo,
        String mensaje,
        Long actividadId,
        Long logroId,
        LocalDateTime creadaEn,
        boolean leida
) {
}
