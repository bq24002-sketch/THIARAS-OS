package com.thiarasos.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracion {

    private final Properties propiedades;

    public Configuracion() {

        propiedades = new Properties();

        cargarConfiguracion();
    }

    private void cargarConfiguracion() {

        try (InputStream archivo =
                     getClass()
                             .getClassLoader()
                             .getResourceAsStream("config.properties")) {

            if (archivo == null) {
                throw new RuntimeException(
                        "No se encontró el archivo config.properties"
                );
            }

            propiedades.load(archivo);

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo cargar la configuración.",
                    e
            );
        }
    }

    public String obtener(String clave) {

        return propiedades.getProperty(clave);
    }

    public String obtener(String clave, String valorPorDefecto) {

        return propiedades.getProperty(
                clave,
                valorPorDefecto
        );
    }
}