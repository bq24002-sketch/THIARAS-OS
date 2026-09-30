package com.thiarasos.persistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EntradaDiario(
        long id,
        long actividadId,
        Long cumplimientoId,
        LocalDate fecha,
        String contenido,
        LocalDateTime creadoEn
) {
}