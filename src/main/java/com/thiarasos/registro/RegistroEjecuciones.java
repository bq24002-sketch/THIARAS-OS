package com.thiarasos.registro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class RegistroEjecuciones {

    private final Path archivo;


    public RegistroEjecuciones() {

        this.archivo =
                Path.of(
                        "data",
                        "recordatorios.log"
                );

        crearArchivoSiNoExiste();
    }


    private void crearArchivoSiNoExiste() {

        try {

            if (!Files.exists(archivo)) {

                Files.createDirectories(
                        archivo.getParent()
                );

                Files.createFile(
                        archivo
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo crear el archivo de registro.",
                    e
            );
        }
    }


    public void registrar(
            String identificador,
            String resultado
    ) {

        String linea =
                LocalDateTime.now()
                        + " | "
                        + resultado
                        + " | "
                        + identificador
                        + System.lineSeparator();

        try {

            Files.writeString(
                    archivo,
                    linea,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {

            System.out.println(
                    "❌ No se pudo escribir en el registro: "
                            + e.getMessage()
            );
        }
    }


    public boolean yaFueProcesado(String identificador) {

        try {

            for (String linea : Files.readAllLines(archivo)) {

                if (linea.contains(" | ENVIADO | " + identificador)
                        || linea.contains(" | ATRASADO_ENVIADO | " + identificador)) {

                    return true;
                }
            }

            return false;

        } catch (IOException e) {

            System.out.println(
                    "❌ No se pudo consultar el registro: "
                            + e.getMessage()
            );

            return false;
        }
    }
}