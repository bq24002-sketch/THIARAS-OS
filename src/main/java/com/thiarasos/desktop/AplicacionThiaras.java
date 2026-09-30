package com.thiarasos.desktop;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import com.thiarasos.config.Configuracion;
import com.thiarasos.servicio.ServicioActividad;
import com.thiarasos.servicio.ServicioDiario;
import com.thiarasos.servicio.ServiciosPersistencia;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.BorderPane;

public final class AplicacionThiaras extends Application {

    @Override
    public void start(Stage escenario) {

        Configuracion configuracion =
                new Configuracion();

        ServicioActividad servicio =
                ServiciosPersistencia.crearServicioActividad(
                        configuracion
                );

        ServicioDiario servicioDiario =
                ServiciosPersistencia.crearServicioDiario(
                        configuracion
                );

        PanelPrincipal panel = new PanelPrincipal(
                servicio,
                servicioDiario
        );

        BorderPane raiz =
        panel.crear();

        Scene escena = new Scene(
                raiz,
                1180,
                760
        );

        escena.getStylesheets().add(
                getClass()
                        .getResource("/thiaras.css")
                        .toExternalForm()
        );

panel.restaurarFondo();
        /*
         * Eliminamos la decoración nativa de Windows.
         * La barra superior de THIARAS será nuestra propia interfaz.
         */
        escenario.initStyle(StageStyle.UNDECORATED);

        escenario.setTitle("THIARAS OS");

        escenario.setMinWidth(940);
        escenario.setMinHeight(620);

        escenario.setScene(escena);

        escenario.show();

        escenario.setOnShown(evento -> {
            escenario.setMaximized(true);
        });

        panel.refrescar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}