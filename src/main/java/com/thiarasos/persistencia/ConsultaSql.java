package com.thiarasos.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class ConsultaSql {

    private ConsultaSql() {
    }

    static String cargar(String nombre) {
        String recurso = "sql/" + nombre;

        try (InputStream archivo = ConsultaSql.class.getClassLoader()
                .getResourceAsStream(recurso)) {
            if (archivo == null) {
                throw new IllegalStateException("No se encontro la consulta " + recurso);
            }

            return new String(archivo.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la consulta " + recurso, e);
        }
    }
}
