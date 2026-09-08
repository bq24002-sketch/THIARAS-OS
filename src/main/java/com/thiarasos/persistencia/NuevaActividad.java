package com.thiarasos.persistencia;

import java.time.LocalDate;
import java.time.LocalTime;

public record NuevaActividad(
        String titulo,
        String materia,
        String contenido,
        Integer duracionMinutos,
        String tipo,
        LocalDate fechaInicio,
        LocalDate fechaVencimiento,
        LocalTime hora,
        boolean recurrente,
        int diaSemana,
        int minutosAnticipacion
) {
}
