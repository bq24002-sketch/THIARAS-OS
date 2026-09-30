package com.thiarasos.desktop;

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.prefs.Preferences;

final class GestorFondo {

    private static final String CLAVE_FONDO =
            "fondo_principal";

    private final Preferences preferencias =
            Preferences.userNodeForPackage(
                    GestorFondo.class
            );

    void seleccionarYAplicar(
            Window ventana,
            BorderPane raiz
    ) {

        FileChooser selector =
                new FileChooser();

        selector.setTitle(
                "Seleccionar fondo de THIARAS OS"
        );

        selector.getExtensionFilters().addAll(

                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png",
                        "*.jpg",
                        "*.jpeg",
                        "*.webp"
                ),

                new FileChooser.ExtensionFilter(
                        "Todos los archivos",
                        "*.*"
                )
        );

        File archivo =
                selector.showOpenDialog(
                        ventana
                );

        if (archivo == null) {
            return;
        }

        aplicar(
                raiz,
                archivo
        );

        preferencias.put(
                CLAVE_FONDO,
                archivo.toURI().toString()
        );
    }

    void restaurar(
            BorderPane raiz
    ) {

        String ruta =
                preferencias.get(
                        CLAVE_FONDO,
                        null
                );

        if (ruta == null) {
            return;
        }

        try {

            File archivo =
                    new File(
                            java.net.URI.create(ruta)
                    );

            if (!archivo.exists()) {
                return;
            }

            aplicar(
                    raiz,
                    archivo
            );

        } catch (Exception ignored) {
            // Si el fondo ya no existe,
            // simplemente conservamos el fondo original.
        }
    }

    private void aplicar(
            BorderPane raiz,
            File archivo
    ) {

        Image imagen =
                new Image(
                        archivo.toURI().toString()
                );

        BackgroundSize tamaño =
                new BackgroundSize(
                        100,
                        100,
                        true,
                        true,
                        false,
                        true
                );

        BackgroundImage fondo =
                new BackgroundImage(
                        imagen,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        tamaño
                );

        raiz.setBackground(
                new Background(fondo)
        );
    }
}