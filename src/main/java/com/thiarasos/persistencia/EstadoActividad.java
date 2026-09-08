package com.thiarasos.persistencia;

import java.time.LocalDateTime;

public record EstadoActividad(
        long actividadId,
        String titulo,
        Integer duracionMinutos,
        String estado,
        Long cumplimientoId,
        LocalDateTime momentoInicio,
        LocalDateTime momentoFin,
        Integer minutosRealizados
) {
}
