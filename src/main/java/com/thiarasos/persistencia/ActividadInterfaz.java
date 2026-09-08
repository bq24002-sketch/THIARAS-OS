package com.thiarasos.persistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActividadInterfaz(
        long id,
        String titulo,
        String materia,
        String contenido,
        Integer duracionMinutos,
        LocalDate fechaInicio,
        LocalDate fechaVencimiento,
        LocalTime hora,
        boolean recurrente,
        boolean activa,
        String estado,
        Long cumplimientoId,
        LocalDateTime momentoInicio,
        LocalDateTime momentoFin,
        Integer minutosRealizados
) {
}
