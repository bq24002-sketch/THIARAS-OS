package com.thiarasos.desktop;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

final class PantallaCarga {

    private final Stage escenario;

    PantallaCarga() {

        escenario = new Stage();

        escenario.initStyle(
                StageStyle.UNDECORATED
        );

        Image imagen = new Image(
                getClass()
                        .getResourceAsStream(
                                "/thiaras-icon.png"
                        )
        );

        ImageView logo = new ImageView(imagen);

        logo.setFitWidth(150);
        logo.setFitHeight(150);
        logo.setPreserveRatio(true);

        Label nombre = new Label(
                "THIARAS OS"
        );

        nombre.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
        """);

        Label estado = new Label(
                "Iniciando..."
        );

        estado.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #BFA8D4;
        """);

        VBox contenido = new VBox(
                14,
                logo,
                nombre,
                estado
        );

        contenido.setAlignment(
                Pos.CENTER
        );

        contenido.setStyle("""
            -fx-background-color:
                linear-gradient(
                    to bottom,
                    #10081C,
                    #21102F
                );
        """);

        Scene escena = new Scene(
                contenido,
                500,
                360
        );

        escena.setFill(null);

        escenario.setScene(escena);
    }

    void mostrar() {
        escenario.show();
    }

    void cerrar() {
        escenario.close();
    }
}