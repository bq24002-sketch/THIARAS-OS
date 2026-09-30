package com.thiarasos.persistencia;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ActividadEditable(
        long id,
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
        List<Recordatorio> recordatorios
) {
}