package com.thiarasos.persistencia;

import java.time.LocalDate;
import java.time.LocalTime;

public record ActividadSemanal(
        long id,
        String titulo,
        String materia,
        LocalDate fecha,
        LocalTime hora,
        Integer duracionMinutos,
        boolean recurrente,
        String estado
) {
}
