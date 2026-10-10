package com.thiarasos.persistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CumplimientoDiario(
        long id,
        LocalDate fecha,
        LocalDateTime momentoInicio,
        LocalDateTime momentoFin,
        Integer minutosRealizados,
        String estado
) {

    @Override
    public String toString() {
        String duracion = minutosRealizados == null
                ? "sin duración"
                : minutosRealizados + " min";

        return fecha
                + " · "
                + estado
                + " · "
                + duracion;
    }
}