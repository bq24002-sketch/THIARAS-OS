package com.thiarasos.persistencia;

public record Recordatorio(
        long id,
        int minutosAnticipacion,
        boolean activo
) {
}