package com.thiarasos.persistencia;

import java.time.LocalTime;

public record BloqueHorarioOcupado(
        long actividadId,
        String titulo,
        int diaSemana,
        LocalTime inicio,
        LocalTime fin
) {
}