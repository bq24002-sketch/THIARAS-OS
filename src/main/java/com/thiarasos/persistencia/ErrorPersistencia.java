package com.thiarasos.persistencia;

public final class ErrorPersistencia extends RuntimeException {

    public ErrorPersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
